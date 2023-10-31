package ch.bbw.zork;import javax.swing.*;import java.util.*;public class Game {	private Player player;	private Parser parser;	private Room currentRoom;	private Room outside, waiting_room, laboratory, hall, patient_room, balcony, operation_room;	private Item needle, fire_extinguisher, cable, metal_bar, crowbar, pillow, fist;	private Enemy zombie, fat_zombie, nurse, doctor;	private static final Random random = new Random();	public Game() {		parser = new Parser(System.in);		fist = new Item("Fist", "Your own fist", 1.0, 100, 0.5, null, 0, true);		outside = new Room("outside the ruined Hospital", 0);		waiting_room = new Room("the waiting room. Something tells you not to stay too long...", 1);		balcony = new Room("the balcony. you can see the city in ruins", 2);		laboratory = new Room("laboratory, a room where research is done to patients", 3);		hall = new Room("the hall. This hall is very long...", 4);		patient_room = new Room("patient room... it gives you chills", 5);		operation_room = new Room("Last room in this floor...  ", 6);		outside.setExits(null, waiting_room, null, null);		waiting_room.setExits(null, balcony, hall, outside);		balcony.setExits(null, waiting_room, null, null);		laboratory.setExits(null, hall, operation_room, null);		hall.setExits(waiting_room,null,patient_room,laboratory);		patient_room.setExits(hall, null, null, null);		operation_room.setExits(laboratory, null, null, null);		currentRoom = outside; // start game outside		// Create the player's inventory with some starting items		Item[] startingInventory = {fist};		player = new Player(100, "PlayerName", currentRoom.getRoomId(), currentRoom.getRoomId(), startingInventory);	}	public void play() {		printWelcome();		boolean finished = false;		while (!finished) {			Command command = parser.getCommand();			finished = processCommand(command);		}		System.out.println("Thank you for playing.  Good bye.");	}	private void printWelcome() {		System.out.println();		System.out.println("Welcome to Zork!");		System.out.println("Zork is a simple adventure game.");		System.out.println("Type 'help' if you need help.");		System.out.println();		System.out.println(currentRoom.longDescription());	}	private boolean processCommand(Command command) {		if (command.isUnknown()) {			System.out.println("I don't know what you mean...");			return false;		}		String commandWord = command.getCommandWord();		switch (commandWord) {			case "help":				printHelp();				break;			case "say":				System.out.println(command.getSecondWord());				break;			case "use":				if (command.hasSecondWord()) {					try {						int itemIndex = Integer.parseInt(command.getSecondWord()) - 1; // Adjust index to start from 0						Item itemToUse = player.findItemByIndex(itemIndex);						if (itemToUse != null) {							// Implement the logic for using the item here							selectItem(itemToUse); // For example, select the item for use							System.out.println("You selected the " + itemToUse.getName() + "!");						} else {							System.out.println("You can't find that item in your inventory.");						}					} catch (NumberFormatException e) {						System.out.println("Invalid item index. Use 'use' followed by the item number.");					}				} else {					System.out.println("Use what? Specify the item number.");				}				break;			case "get":				generateRandomItem();				break;			case "go":				goRoom(command);				break;			case "inv":				printInventory();				break;			case "map":				printMap(currentRoom.getRoomId());				break;			case "search":				searchRoom();				break;			case "drop":				if (command.hasSecondWord()) {					String indexOrName = command.getSecondWord();					if (isNumeric(indexOrName)) {						// If it's a number, try to drop the item by index						int index = Integer.parseInt(indexOrName) - 1; // Adjust index to start from 0						if (index >= 0 && index < player.getInventory().length) {							Item itemToDrop = player.findItemByIndex(index);							player.removeFromInventory(itemToDrop);							System.out.println("You dropped the " + itemToDrop.getName());						} else {							System.out.println("Invalid item index. Check your inventory and try again.");						}					} else {						// If it's not a number, try to drop the item by name						Item itemToDrop = player.findItemByName(indexOrName);						if (itemToDrop != null) {							player.removeFromInventory(itemToDrop);							System.out.println("You dropped the " + itemToDrop.getName());						} else {							System.out.println("You don't have an item with that name in your inventory.");						}					}				} else {					System.out.println("Drop what? Specify the item number or name.");				}				break;			case "quit":				if (command.hasSecondWord()) {					System.out.println("Quit what?");				} else {					return true; // signal that we want to quit				}				break;		}		return false;	}	private void printHelp() {		System.out.println("You are lost. You are alone. You wander");		System.out.println("around at the abandoned Hospital, Colorado.");		System.out.println();		System.out.println("Your command words are:");		System.out.println(parser.showCommands());		System.out.println("Good luck, soldier");	}	private void printInventory() {		// Access the inventory through the player instance		List<Item> playerInventory = List.of(player.getInventory());		if (playerInventory != null && !playerInventory.isEmpty()) {			System.out.println("You look through your inventory.");			System.out.println("Inside you have:");			for (Item item : playerInventory) {				String star = item.isHasStar() ? "★" : ""; // Add a star if the item has a star				System.out.println(star + " " + item.getName());			}		} else {			System.out.println("Your inventory is empty.");		}	}	private void goRoom(Command command) {		if (!command.hasSecondWord()) {			System.out.println("Go where?");		} else {			String direction = command.getSecondWord();			// Try to leave current room.			Room nextRoom = currentRoom.nextRoom(direction);			if (nextRoom == null)				System.out.println("There is no door!");			else {				currentRoom = nextRoom;				if (currentRoom.shortDescription().equals("outside G block on Peninsula campus")) {					System.out.println("Das Fenster ist offen, brrrrrrr");				}				System.out.println(currentRoom.longDescription());			}		}	}	public void printMap(int roomID) {		System.out.println("+--------------+        +--------------+        +--------------+");		System.out.println("|              |        |              |        |              |");		System.out.println("|   Outside    |--------| Waiting room |--------|    Balcony   |");		System.out.println("|              |        |              |        |              |");		System.out.println("+--------------+        +--------------+        +--------------+");		System.out.println("                               |");		System.out.println("                               |");		System.out.println("+--------------+        +--------------+");		System.out.println("|              |        |              |");		System.out.println("|  Laboratory  |--------|     Hall     |");		System.out.println("|              |        |              |");		System.out.println("+--------------+        +--------------+");		System.out.println("       |                       |");		System.out.println("       |                       |");		System.out.println("+--------------+        +--------------+");		System.out.println("|              |        |              |");		System.out.println("|Operation room|        | Patient room |");		System.out.println("|              |        |              |");		System.out.println("+--------------+        +--------------+");		String wherePlayerIs;		switch (roomID) {			case 0:				wherePlayerIs = "Outside";				break;			case 1:				wherePlayerIs = "Waiting room";				break;			case 2:				wherePlayerIs = "Balcony";				break;			case 3:				wherePlayerIs = "Laboratory";				break;			case 4:				wherePlayerIs = "Hall";				break;			case 5:				wherePlayerIs = "Operation room";				break;			case 6:				wherePlayerIs = "Patient room";				break;			default:				throw new IllegalStateException("Unexpected value: " + roomID);		}		System.out.println("You are currently in the " + wherePlayerIs);	}	public void generateRandomItem() {		String[] itemNames = {"Needle", "Scalpel", "Syringe", "Stethoscope", "Tweezers", "Forceps", "Medical Hammer", "Blood Pressure Cuff", "Thermometer"};		String name = itemNames[random.nextInt(itemNames.length)];		String description = "Description of " + name;		double damage = random.nextDouble() * 10; // Adjust the damage range as needed		int durability = random.nextInt(10) + 1;		double weight = random.nextDouble() * 2; // Adjust the weight range as needed		String[] perks = null;		int hp = random.nextInt(10) + 1;		// Create a new Item object		Item randomItem = new Item(name, description, damage, durability, weight, perks, hp, false);		// Add the Item object directly to the player's inventory		player.addToInventory(randomItem);		System.out.println("You found a " + name + "!");	}	public void selectItem(Item item2Bselected) {		removeStarsFromAllItems(); // Remove stars from all items in the inventory		item2Bselected.setHasStar(true); // Set the selected item to have a star	}	public void removeStarsFromAllItems() {		for (Item item : player.getInventory()) {			if (item.isHasStar()) {				item.setHasStar(false); // Remove the star from the item			}		}	}	public void searchRoom() {		if (!currentRoom.isSearched()) {			System.out.println("Dieser Raum wurde schon gesucht.");		} else {			currentRoom.setSearched(true);			generateRandomItem();		}	}	public boolean isNumeric(String str) {		try {			Double.parseDouble(str);			return true;		} catch (NumberFormatException e) {			return false;		}	}}