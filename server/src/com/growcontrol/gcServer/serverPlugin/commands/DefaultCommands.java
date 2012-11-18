package com.growcontrol.gcServer.serverPlugin.commands;

import com.growcontrol.gcServer.gcServer;
import com.growcontrol.gcServer.serverPlugin.events.gcServerEvent.EventPriority;
import com.growcontrol.gcServer.serverPlugin.events.gcServerEventCommand;
import com.growcontrol.gcServer.serverPlugin.listeners.gcServerListenerCommand;

public class DefaultCommands extends gcServerListenerCommand {


	public DefaultCommands() {
		setPriority(EventPriority.LOWEST);
		// basic commands
		add("stop")
			.addAlias("exit")
			.addAlias("quit")
			.addAlias("shutdown")
			.setUsage("Stops and closes the server.");
		add("kill")
			.setUsage("Emergency shutdown, no saves or power-downs. Don't use this unless you need to.");
//		add("start")
		add("pause")
			.setUsage("Pauses or resumes the scheduler and some plugin tasks. Optional argument: [on/off/true/false/1/0]");
		add("clear")
			.addAlias("cls")
			.setUsage("Clears the console screen.");
//		add("help")
//			.addAlias("?")
		add("show")
			.setUsage("Displays additional information.");
		add("version")
			.setUsage("Displays the current running version, and the latest available (if enabled)");
//		add("say")
//			.addAlias("broadcast")
//			.setUsage("");
//		add("list")
//		// input / output
//		add("set")
//		add("get")
//		add("watch")
		// tools
		add("ping")
			.setUsage("");
		add("threads")
			.setUsage("Displays number of loaded threads, and optional details.");
	}


	@Override
	public boolean onCommand(gcServerEventCommand event) {
		if(event.isHandled()) return false;
		if(!event.hasCommand()) return false;
//System.out.println("COMMAND: "+event.getCommandStr());
//TODO: command aliases aren't working with this yet
//TODO: update: should be working now, I think, untested
		// basic commands
		if(event.equals("stop")) {
			gcServer.Shutdown();
			return true;
		} else if(event.equals("kill")) {
			System.exit(0);
		} else if(event.equals("pause")) {
			return true;
		} else if(event.equals("clear")) {
			return true;
		} else if(event.equals("show")) {
			return true;
		} else if(event.equals("version")) {
			return true;
		// tools
		} else if(event.equals("ping")) {
			return true;
		} else if(event.equals("threads")) {
			return true;
		}
		return false;
	}


//		// start
//		if(command.equals("start")) {
//			gcScheduler.pauseAll(false);
//			return true;
//		}
//		// stop
//		if(command.equals("stop")) {
//			gcScheduler.pauseAll(true);
//			return true;
//		}
//		// pause
//		if(command.equals("pause")) {
//			gcScheduler.pauseAll();
//			return true;
//		}
//		// clear
//		if(command.equals("clear")) {
//			gcServer.log.clear();
//			return true;
//		}
//		// help
//		if(command.equals("help")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
//			gcServer.log.info("[ Basic Commands ]");
//			gcServer.log.info("version - Displays the current running version.");
//			gcServer.log.info("start - Starts the scheduler.");
//			gcServer.log.info("stop  - Stops the scheduler.");
//			gcServer.log.info("pause - Pauses the scheduler.");
//			gcServer.log.info("clear - Clears the screen.");
//			gcServer.log.info("say - Broadcasts a message.");
//			gcServer.log.info("list plugins - Lists the loaded plugins.");
//			gcServer.log.info("list devices - Lists the loaded devices.");
//			gcServer.log.info("list outputs - Lists the available outputs.");
//			gcServer.log.info("list inputs  - Lists the available inputs.");
//			gcServer.log.info("[ Tools ]");
//			gcServer.log.info("ping - ");
//			gcServer.log.info("threads - ");
// input / output
//set
//get
//watch
//stop
//exit
//quit
//shutdown
//kill
//			return true;
//		}
//		// version
//		if(command.equals("version")) {
//			//TODO:
//			gcServer.log.info("GrowControl "+gcServer.version);
//			return true;
//		}
//		// say
//		if(command.equals("say")) {
//			String msg = "";
//			for(String line : args) msg += " "+line;
//			gcServer.log.info("Server says:"+msg);
//			return true;
//		}

		// list plugins/devices/inputs/outputs
//		if(command.equals("list")) {
//			if(args.length >= 1) {
//				if(args[0].equalsIgnoreCase("plugins")) {
////					gcServerPluginLoader.listPlugins();
//					return true;
//				} else if(args[0].equalsIgnoreCase("devices")) {
//					listDevices();
//					return true;
//				} else if(args[0].equalsIgnoreCase("outputs")) {
//					listOutputs();
//					return true;
//				} else if(args[0].equalsIgnoreCase("inputs")) {
//					listInputs();
//					return true;
//				}
//			}
//			gcServer.log.info("Usage: "+command.getUsage());
//			return true;
//		}
//		if(command.equals("plugins")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
////			gcPluginLoader.listPlugins();
//			return true;
//		}
//		// list devices
//		if(command.equals("devices")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
////			gcServer.deviceLoader.listDevices();
//			return true;
//		}
//
//		// set input / output
//		if(command.equals("set")) {
////			if(gcServerPluginLoader.doOutput(args)) {
////				String msg = ""; for(String arg : args) msg += arg+" ";
////				gcServer.log.debug("set> "+msg);
////				return true;
////			}
////			String msg = ""; for(String arg : args) msg += arg+" ";
////			gcServer.log.warning("Failed to find an output plugin! "+msg);
//			return true;
//		}
//		// get input / output
//		if(command.equals("get")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
//			return true;
//		}
//		// watch input / output
//		if(command.equals("watch")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
//			return true;
//		}
//
//		// ping
//		if(command.equals("ping")) {
//			//TODO:
//			gcServer.log.warning("command not yet implemented");
//			return true;
//		}
//
//		return false;
//	}


//	private static void listDevices() {
//	}
//	private static void listOutputs() {
//	}
//	private static void listInputs() {
//	}


//	if(command.equalsIgnoreCase("threads")) {
//	// Find the root thread group
//	ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
//	while (root.getParent() != null) {
//	    root = root.getParent();
//	}
//GrowControl.log.severe(Integer.toString(root.activeCount()));
//	// Visit each thread group
//GrowControl.log.severe(Integer.toString(Thread.activeCount()));
//	visitThread(root, 0);
//	return true;
//}
//else if(line.equals("list coms"))
//Serial.listPorts();
//	// This method recursively visits all thread groups under `group'.
//	private static void visitThread(ThreadGroup group, int level) {
//	    // Get threads in `group'
//	    int numThreads = group.activeCount();
//	    Thread[] threads = new Thread[numThreads*2];
//	    numThreads = group.enumerate(threads, false);
//	    // Enumerate each thread in `group'
//	    for(int i=0; i<numThreads; i++)
//	        Thread thread = threads[i];
//	    // Get thread subgroups of `group'
//	    int numGroups = group.activeGroupCount();
//	    ThreadGroup[] groups = new ThreadGroup[numGroups*2];
//	    numGroups = group.enumerate(groups, false);
//	    // Recursively visit each subgroup
//	    for(int i=0; i<numGroups; i++) {
//	    	visitThread(groups[i], level+1);
//	    }
//	}


}