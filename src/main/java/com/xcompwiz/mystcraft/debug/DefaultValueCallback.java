package com.xcompwiz.mystcraft.debug;

import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugValueCallback;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public abstract class DefaultValueCallback implements DebugValueCallback {

	public static class CallbackSetNotSupported extends CommandException {
		private static final long	serialVersionUID	= 5894138465820317546L;

		public CallbackSetNotSupported() {
			super("myst.debug.callback.set.unsupported");
		}
	}

	public static class CallbackReadNotSupported extends CommandException {
		private static final long	serialVersionUID	= 3593269422046054872L;

		public CallbackReadNotSupported() {
			super("myst.debug.callback.read.unsupported");
		}
	}

	@Override
	public String get(ICommandSender agent) throws CallbackReadNotSupported {
		throw new CallbackReadNotSupported();
	}

	@Override
	public void set(ICommandSender agent, String state) throws CallbackSetNotSupported {
		throw new CallbackSetNotSupported();
	}

}
