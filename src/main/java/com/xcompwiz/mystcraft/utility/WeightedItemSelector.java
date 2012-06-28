package com.xcompwiz.mystcraft.utility;

import java.util.Collection;
import java.util.Random;

import com.xcompwiz.mystcraft.logging.LoggerUtils;

public class WeightedItemSelector {

	public interface IWeightProvider {
		public float getItemWeight(Object item);
	}

	public interface IWeightedItem {
		/**
		 * The weight is used to determine how often the item is chosen (higher = more often; 0 = no chance). Note that in a
		 * collection of items with no chance, the system will default to an even distribution chance
		 */
		public float getWeight();
	}

	public static class WeightProviderDefault implements IWeightProvider {
		public static WeightProviderDefault	instance	= new WeightProviderDefault();

		private WeightProviderDefault() {};

		@Override
		public float getItemWeight(Object item) {
			if (item instanceof IWeightedItem) { return ((IWeightedItem) item).getWeight(); }
			return 1.0F;
		}
	}

	public static <T> float getTotalWeight(Collection<T> collection) {
		return getTotalWeight(collection, WeightProviderDefault.instance);
	}

	public static <T> float getTotalWeight(Collection<T> collection, IWeightProvider wgtprov) {
		float total = 0.0F;
		for (Object item : collection) {
			total += wgtprov.getItemWeight(item);
		}
		return total;
	}

	public static <T> T getRandomItem(Random rand, Collection<T> collection) {
		return getRandomItem(rand, collection, WeightProviderDefault.instance);
	}

	public static <T> T getRandomItem(Random rand, Collection<T> collection, IWeightProvider wgtprov) {
		if (collection == null) return null;
		float max = getTotalWeight(collection, wgtprov);
		if (max <= 0) { return getRandomItemEvenly(rand, collection); }
		T last = null;
		float selection = rand.nextFloat() * max;
		for (T item : collection) {
			float weight = wgtprov.getItemWeight(item);
			selection -= weight;
			if (weight > 0) {
				if (selection <= 0) { return item; }
				last = item;
			}
		}
		LoggerUtils.warn("Something odd happened when selecting a random item from a weighted collection.");
		return last;
	}

	public static <T> T getRandomItemEvenly(Random rand, Collection<T> collection) {
		T last = null;
		float selection = rand.nextFloat() * collection.size();
		for (T item : collection) {
			selection -= 1;
			if (selection <= 0) { return item; }
			last = item;
		}
		LoggerUtils.warn("Something odd happened when selecting a random item from an evenly weighted collection.");
		return last;
	}
}
