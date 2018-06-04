/* Contains constant data, including:
	Slayer Master Info:
		Master Name
		List of Tasks
		Combat Consideration

	Slayer Task Info:
		Monster Name
		Combat Level Required
		Slayer Level Required
		Free Block Status

	Slayer Task Info (By Master):
		Slayer Master
		Task Weight

	Also contains a comparator used to sort lists based on user preference.
 */

import java.util.Comparator;
import java.lang.StringBuilder;

class Constants
{
	/* The Number of Masters. */
	static int num_masters = 7;

	/* Contains information about a given slayer master. */
	static class Master
	{
		/* Intrinsic Values. */
		String name;
		Task[] tasks;
		double points;
		boolean cmb_app;

		/* User-inputted values. */
		boolean init = false;
		boolean enable = false;

		/* Constructor. */
		Master(String n, Task[] t, double p, boolean c)
		{
			name = n;
			tasks = t;
			points = p;
			cmb_app = c;
		}

		/* Takes in user input for whether or not to enable this master. */
		void user_in(boolean e)
		{
			this.init = true;
			this.enable = e;
		}

		@Override
		public String toString()
		{
			int i;
			StringBuilder rv = new StringBuilder(this.name + ": " + (this.enable ? "Enabled" : "Disabled"));

			if (this.enable) {
				rv.append("\n\nList of Tasks:");

				for (i = 0; i < this.tasks.length; i++)
					rv.append("\n\n");
					rv.append(this.tasks[i].toString());

			}

			return rv.toString();
		}
	}

	/* Contains info about a particular task from a particular master. */
	static class Task
	{
		/* Intrinsic values. */
		String name;
		int s_reqt;
		int cmb_rec;
		boolean fblock;

		/* Master-specific values. */
		String master;
		int weight;

		/* User-inputted values. */
		boolean init = false;
		boolean poss = false;
		int pref = 0;

		/* Constructor. Only takes in constant values. */
		Task(String n, int s, int c, boolean f,
		             String m, int w)
		{
			master = m;
			weight = w;

			name = n;
			s_reqt = s;
			cmb_rec = c;
			fblock = f;
		}

		/* Takes in user input for possibility and preference. */
		void user_in(boolean po, int pr)
		{
			this.init = true;
			this.poss = po;
			this.pref = pr;
		}

		@Override
		public String toString()
		{
			return "Task: " + this.name + " from " + this.master + ":\n" +
				"\tSlayer Level Req: " + this.s_reqt + "\n" +
				"\tCombat Level Rec: " + this.cmb_rec + "\n" +
				"\tFree Block: " + this.fblock + "\n" +
				(this.init ? "\tPossible? " + this.poss + "\n" +
					"\tPreference: " + this.pref :
					"\t NOT_YET_INITIALIZED");
		}

	}

	/* Compares two tasks, returning positive if a < b. */
	static class sortPref
		implements Comparator<Task>
	{
		@Override
		public int compare(Task a, Task b)
		{
			if (!a.init || !b.init)
				System.out.println("Compared an un-initialized task:\n\n" +
						a.toString() + "\n\n" + b.toString());
			return b.pref - a.pref;
		}
	}

	/* For each slayer master, their task details. */
	// https://twitter.com/JagexAsh/status/860859015248711680
	// https://www.reddit.com/r/2007scape/comments/5zya6b/slayer_task_weightings_for_every_master/

	/* KRYSTILIA */
	private static Task[] _krystilia_tasks = {
		new Task("Ankou",               1,      0,      false,  "Krystilia",    6),
		new Task("Aviansies",           1,      0,      true,   "Krystilia",    7),
		new Task("Bandits",             1,      0,      false,  "Krystilia",    4),
		new Task("Bears",               1,      0,      false,  "Krystilia",    6),
		new Task("Black Demons",        1,      0,      false,  "Krystilia",    7),
		new Task("Black Dragons",       1,      0,      false,  "Krystilia",    4),
		new Task("Bosses",              1,      0,      true,   "Krystilia",    8),
		new Task("Chaos Druids",        1,      0,      false,  "Krystilia",    5),
		new Task("Dark Warriors",       1,      0,      false,  "Krystilia",    4),
		new Task("Earth Warriors",      1,      0,      false,  "Krystilia",    6),
		new Task("Ents",                1,      0,      false,  "Krystilia",    5),
		new Task("Fire Giants",         1,      0,      false,  "Krystilia",    7),
		new Task("Greater Demons",      1,      0,      false,  "Krystilia",    8),
		new Task("Green Dragons",       1,      0,      false,  "Krystilia",    4),
		new Task("Hellhounds",          1,      0,      false,  "Krystilia",    7),
		new Task("Ice Giants",          1,      0,      false,  "Krystilia",    6),
		new Task("Ice Warriors",        1,      0,      false,  "Krystilia",    7),
		new Task("Lava Dragons",        1,      0,      false,  "Krystilia",    3),
		new Task("Lesser Demons",       1,      0,      false,  "Krystilia",    6),
		new Task("Magic Axes",          1,      0,      false,  "Krystilia",    7),
		new Task("Mammoths",            1,      0,      false,  "Krystilia",    6),
		new Task("Revenants",           1,      0,      false,  "Krystilia",    5),
		new Task("Rogues",              1,      0,      false,  "Krystilia",    5),
		new Task("Scorpions",           1,      0,      false,  "Krystilia",    6),
		new Task("Skeletons",           1,      0,      false,  "Krystilia",    5),
		new Task("Spiders",             1,      0,      false,  "Krystilia",    6),
		new Task("Spiritual Creatures", 63,     0,      false,  "Krystilia",    6)
	};
	private static Master krystilia = new Master("Krystilia", _krystilia_tasks, 44.375, false);

	/* TURAEL */
	private static Task[] _turael_tasks = {
		new Task("Banshees",            15,     20,     false,  "Turael",       8),
		new Task("Bats",                1,      5,      false,  "Turael",       7),
		new Task("Bears",               1,      13,     false,  "Turael",       7),
		new Task("Birds",               1,      0,      false,  "Turael",       6),
		new Task("Cave Bugs",           7,      0,      false,  "Turael",       8),
		new Task("Cave Crawlers",       10,     10,     false,  "Turael",       8),
		new Task("Cave Slimes",         17,     15,     false,  "Turael",       8),
		new Task("Cows",                1,      5,      false,  "Turael",       8),
		new Task("Crawling Hands",      5,      0,      false,  "Turael",       8),
		new Task("Desert Lizards",      22,     15,     false,  "Turael",       8),
		new Task("Dogs",                1,      15,     false,  "Turael",       7),
		new Task("Dwarves",             1,      6,      false,  "Turael",       7),
		new Task("Ghosts",              1,      13,     false,  "Turael",       7),
		new Task("Goblins",             1,      0,      false,  "Turael",       7),
		new Task("Icefiends",           1,      20,     false,  "Turael",       8),
		new Task("Kalphites",           1,      15,     false,  "Turael",       6),
		new Task("Minotaurs",           1,      7,      false,  "Turael",       7),
		new Task("Monkeys",             1,      0,      false,  "Turael",       6),
		new Task("Rats",                1,      0,      false,  "Turael",       7),
		new Task("Scorpions",           1,      7,      false,  "Turael",       7),
		new Task("Skeletons",           1,      15,     false,  "Turael",       7),
		new Task("Spiders",             1,      0,      false,  "Turael",       6),
		new Task("Wolves",              1,      20,     false,  "Turael",       7),
		new Task("Zombies",             1,      10,     false,  "Turael",       7)
	};
	private static Master turael = new Master("Turael", _turael_tasks, 0, true);

	/* MAZCHNA */
	private static Task[] _mazchna_tasks = {
		new Task("Banshees",            15,     20,     false,  "Mazchna",      8),
		new Task("Bats",                1,      5,      false,  "Mazchna",      7),
		new Task("Bears",               1,      13,     false,  "Mazchna",      6),
		new Task("Catablepons",         1,      35,     false,  "Mazchna",      8),
		new Task("Cave Bugs",           7,      0,      false,  "Mazchna",      8),
		new Task("Cave Crawlers",       10,     10,     false,  "Mazchna",      8),
		new Task("Cave Slimes",         17,     15,     false,  "Mazchna",      8),
		new Task("Cockatrice",          25,     25,     false,  "Mazchna",      8),
		new Task("Crawling Hands",      5,      0,      false,  "Mazchna",      8),
		new Task("Desert Lizards",      22,     15,     false,  "Mazchna",      8),
		new Task("Dogs",                1,      15,     false,  "Mazchna",      7),
		new Task("Earth Warriors",      1,      35,     false,  "Mazchna",      6),
		new Task("Flesh Crawlers",      1,      15,     false,  "Mazchna",      7),
		new Task("Ghosts",              1,      13,     false,  "Mazchna",      7),
		new Task("Ghouls",              1,      25,     false,  "Mazchna",      7),
		new Task("Hill Giants",         1,      25,     false,  "Mazchna",      7),
		new Task("Hobgoblins",          1,      20,     false,  "Mazchna",      7),
		new Task("Ice Warriors",        1,      45,     false,  "Mazchna",      7),
		new Task("Kalphites",           1,      15,     false,  "Mazchna",      6),
		new Task("Killerwatts",         37,     50,     false,  "Mazchna",      6),
		new Task("Mogres",              32,     30,     false,  "Mazchna",      8),
		new Task("Pyrefiends",          30,     25,     false,  "Mazchna",      8),
		new Task("Rockslugs",           20,     20,     false,  "Mazchna",      8),
		new Task("Scorpions",           1,      7,      false,  "Mazchna",      7),
		new Task("Shades",              1,      30,     false,  "Mazchna",      8),
		new Task("Skeletons",           1,      15,     false,  "Mazchna",      7),
		new Task("Vampyres",            1,      35,     false,  "Mazchna",      6),
		new Task("Wall Beasts",         35,     30,     false,  "Mazchna",      7),
		new Task("Wolves",              1,      20,     false,  "Mazchna",      7),
		new Task("Zombies",             1,      10,     false,  "Mazchna",      7)
	};
	private static Master mazchna = new Master("Mazchna", _mazchna_tasks, 3.03, true);

	/* VANNAKA */
	private static Task[] _vannaka_tasks = {
		new Task("Aberrant Spectres",   60,     65,     false,  "Vannaka",      8),
		new Task("Abyssal Demons",      85,     85,     false,  "Vannaka",      5),
		new Task("Ankou",               1,      40,     false,  "Vannaka",      7),
		new Task("Banshees",            15,     20,     false,  "Vannaka",      6),
		new Task("Basilisks",           40,     40,     false,  "Vannaka",      8),
		new Task("Bloodveld",           50,     50,     false,  "Vannaka",      8),
		new Task("Blue Dragons",        1,      65,     false,  "Vannaka",      7),
		new Task("Brine Rats",          47,     45,     false,  "Vannaka",      7),
		new Task("Bronze Dragons",      1,      75,     false,  "Vannaka",      7),
		new Task("Cave Bugs",           7,      0,      false,  "Vannaka",      7),
		new Task("Cave Crawlers",       10,     10,     false,  "Vannaka",      7),
		new Task("Cave Slimes",         17,     15,     false,  "Vannaka",      7),
		new Task("Cockatrice",          25,     25,     false,  "Vannaka",      8),
		new Task("Crawling Hands",      5,      0,      false,  "Vannaka",      6),
		new Task("Crocodile",           1,      50,     false,  "Vannaka",      6),
		new Task("Dagannoth",           1,      75,     false,  "Vannaka",      7),
		new Task("Desert Lizards",      22,     15,     false,  "Vannaka",      7),
		new Task("Dust Devils",         65,     70,     false,  "Vannaka",      8),
		new Task("Earth Warriors",      1,      35,     false,  "Vannaka",      6),
		new Task("Elves",               1,      70,     false,  "Vannaka",      7),
		new Task("Fever Spiders",       42,     40,     false,  "Vannaka",      7),
		new Task("Fire Giants",         1,      65,     false,  "Vannaka",      7),
		new Task("Gargoyles",           75,     80,     false,  "Vannaka",      5),
		new Task("Ghouls",              1,      25,     false,  "Vannaka",      7),
		new Task("Green Dragons",       1,      52,     false,  "Vannaka",      6),
		new Task("Harpie Bug Swarms",   25,     45,     false,  "Vannaka",      8),
		new Task("Hellhounds",          1,      75,     false,  "Vannaka",      7),
		new Task("Hill Giants",         1,      25,     false,  "Vannaka",      7),
		new Task("Hobgoblins",          1,      20,     false,  "Vannaka",      7),
		new Task("Ice Giants",          1,      50,     false,  "Vannaka",      7),
		new Task("Ice Warriors",        1,      45,     false,  "Vannaka",      7),
		new Task("Infernal Mages",      45,     40,     false,  "Vannaka",      8),
		new Task("Jellies",             52,     57,     false,  "Vannaka",      8),
		new Task("Jungle Horrors",      1,      65,     false,  "Vannaka",      8),
		new Task("Kalphites",           1,      15,     false,  "Vannaka",      7),
		new Task("Killerwatts",         37,     50,     false,  "Vannaka",      6),
		new Task("Kurasks",             70,     65,     false,  "Vannaka",      7),
		new Task("Lesser Demons",       1,      60,     false,  "Vannaka",      7),
		new Task("Mogres",              32,     30,     false,  "Vannaka",      7),
		new Task("Molanisks",           39,     50,     false,  "Vannaka",      7),
		new Task("Moss Giants",         1,      40,     false,  "Vannaka",      7),
		new Task("Nechryael",           80,     85,     false,  "Vannaka",      5),
		new Task("Ogres",               1,      40,     false,  "Vannaka",      7),
		new Task("Otherworldly Beings", 1,      40,     false,  "Vannaka",      8),
		new Task("Pyrefiends",          30,     25,     false,  "Vannaka",      8),
		new Task("Rockslugs",           20,     20,     false,  "Vannaka",      7),
		new Task("Sea Snakes",          1,      50,     false,  "Vannaka",      6),
		new Task("Shades",              1,      30,     false,  "Vannaka",      8),
		new Task("Shadow Warriors",     1,      60,     false,  "Vannaka",      8),
		new Task("Spiritual Creatures", 63,     60,     false,  "Vannaka",      8),
		new Task("Terror Dogs",         40,     60,     false,  "Vannaka",      6),
		new Task("Trolls",              1,      60,     false,  "Vannaka",      7),
		new Task("Turoth",              55,     60,     false,  "Vannaka",      8),
		new Task("Vampyres",            1,      35,     false,  "Vannaka",      7),
		new Task("Wall Beasts",         35,     30,     false,  "Vannaka",      6),
		new Task("Werewolves",          1,      60,     false,  "Vannaka",      7)
	};
	private static Master vannaka = new Master("Vannaka", _vannaka_tasks, 7.1, true);

	/* CHAELDAR */
	private static Task[] _chaeldar_tasks = {
		new Task("Aberrant Spectres",   60,     65,     false,  "Chaeldar",     8),
		new Task("Abyssal Demons",      85,     85,     false,  "Chaeldar",     12),
		new Task("Aviansies",           1,      0,      true,   "Chaeldar",     9),
		new Task("Banshees",            15,     20,     false,  "Chaeldar",     5),
		new Task("Basilisks",           40,     40,     false,  "Chaeldar",     7),
		new Task("Black Demons",        1,      80,     false,  "Chaeldar",     10),
		new Task("Bloodveld",           50,     50,     false,  "Chaeldar",     8),
		new Task("Blue Dragons",        1,      65,     false,  "Chaeldar",     8),
		new Task("Brine Rats",          47,     45,     false,  "Chaeldar",     7),
		new Task("Bronze Dragons",      1,      75,     false,  "Chaeldar",     11),
		new Task("Cave Crawlers",       10,     10,     false,  "Chaeldar",     5),
		new Task("Cave Horrors",        58,     85,     false,  "Chaeldar",     10),
		new Task("Cave Kraken",         87,     80,     false,  "Chaeldar",     12),
		new Task("Cave Slimes",         17,     15,     false,  "Chaeldar",     6),
		new Task("Cockatrice",          25,     25,     false,  "Chaeldar",     6),
		new Task("Dagannoth",           1,      75,     false,  "Chaeldar",     11),
		new Task("Desert Lizards",      22,     15,     false,  "Chaeldar",     5),
		new Task("Dust Devils",         65,     70,     false,  "Chaeldar",     9),
		new Task("Elves",               1,      70,     false,  "Chaeldar",     8),
		new Task("Fever Spiders",       42,     40,     false,  "Chaeldar",     7),
		new Task("Fire Giants",         1,      65,     false,  "Chaeldar",     12),
		new Task("Fossil Wyverns",      66,     0,      true,   "Chaeldar",     7),
		new Task("Gargoyles",           75,     80,     false,  "Chaeldar",     11),
		new Task("Greater Demons",      1,      75,     false,  "Chaeldar",     9),
		new Task("Harpie Bug Swarms",   25,     45,     false,  "Chaeldar",     6),
		new Task("Hellhounds",          1,      75,     false,  "Chaeldar",     9),
		new Task("Infernal Mages",      45,     40,     false,  "Chaeldar",     7),
		new Task("Iron Dragons",        1,      80,     false,  "Chaeldar",     12),
		new Task("Jellies",             52,     57,     false,  "Chaeldar",     10),
		new Task("Jungle Horrors",      1,      65,     false,  "Chaeldar",     10),
		new Task("Kalphites",           1,      15,     false,  "Chaeldar",     11),
		new Task("Kurasks",             70,     65,     false,  "Chaeldar",     12),
		new Task("Lesser Demons",       1,      60,     false,  "Chaeldar",     9),
		new Task("Lizardmen",           1,      0,      true,   "Chaeldar",     8),
		new Task("Mogres",              32,     30,     false,  "Chaeldar",     6),
		new Task("Molanisks",           39,     50,     false,  "Chaeldar",     6),
		new Task("Mutated Zygomites",   57,     60,     false,  "Chaeldar",     7),
		new Task("Nechryael",           80,     85,     false,  "Chaeldar",     12),
		new Task("Pyrefiends",          30,     25,     false,  "Chaeldar",     6),
		new Task("Rockslugs",           20,     20,     false,  "Chaeldar",     5),
		new Task("Shadow Warriors",     1,      60,     false,  "Chaeldar",     8),
		new Task("Skeletal Wyverns",    72,     70,     false,  "Chaeldar",     7),
		new Task("Spiritual Creatures", 63,     60,     false,  "Chaeldar",     12),
		new Task("Steel Dragons",       1,      85,     false,  "Chaeldar",     9),
		new Task("Trolls",              1,      60,     false,  "Chaeldar",     11),
		new Task("Turoth",              55,     60,     false,  "Chaeldar",     10),
		new Task("TzHaar",              1,      0,      true,   "Chaeldar",     10), //TODO
		new Task("Wall Beasts",         35,     30,     false,  "Chaeldar",     6)
	};
	private static Master chaeldar = new Master("Chaeldar", _chaeldar_tasks, 17.75, true);

	/* NIEVE */
	private static Task[] _nieve_tasks = {
		new Task("Aberrant Spectres",   60,     65,     false,  "Nieve",        6),
		new Task("Abyssal Demons",      85,     85,     false,  "Nieve",        9),
		new Task("Adamant Dragons",     1,      0,      false,  "Nieve",        2),
		new Task("Ankou",               1,      40,     false,  "Nieve",        5),
		new Task("Aviansies",           1,      0,      true,   "Nieve",        6),
		new Task("Black Demons",        1,      80,     false,  "Nieve",        9),
		new Task("Black Dragons",       1,      80,     false,  "Nieve",        6),
		new Task("Bloodveld",           50,     50,     false,  "Nieve",        9),
		new Task("Blue Dragons",        1,      65,     false,  "Nieve",        4),
		new Task("Bosses",              1,      0,      true,   "Nieve",        8),
		new Task("Brine Rats",          47,     45,     false,  "Nieve",        3),
		new Task("Cave Horrors",        58,     85,     false,  "Nieve",        5),
		new Task("Cave Kraken",         87,     80,     false,  "Nieve",        6),
		new Task("Dagannoth",           1,      75,     false,  "Nieve",        8),
		new Task("Dark Beasts",         90,     90,     false,  "Nieve",        5),
		new Task("Dust Devils",         65,     70,     false,  "Nieve",        6),
		new Task("Elves",               1,      70,     false,  "Nieve",        4),
		new Task("Fire Giants",         1,      65,     false,  "Nieve",        9),
		new Task("Fossil Wyverns",      66,     0,      true,   "Nieve",        5),
		new Task("Gargoyles",           75,     80,     false,  "Nieve",        6),
		new Task("Greater Demons",      1,      75,     false,  "Nieve",        7),
		new Task("Hellhounds",          1,      75,     false,  "Nieve",        8),
		new Task("Iron Dragons",        1,      80,     false,  "Nieve",        5),
		new Task("Kalphites",           1,      15,     false,  "Nieve",        9),
		new Task("Kurasks",             70,     65,     false,  "Nieve",        3),
		new Task("Lizardmen",           1,      0,      true,   "Nieve",        8),
		new Task("Mithril Dragons",     1,      0,      true,   "Nieve",        5),
		new Task("Mutated Zygomites",   57,     60,     false,  "Nieve",        2),
		new Task("Nechryael",           80,     85,     false,  "Nieve",        7),
		new Task("Red Dragons",         1,      0,      true,   "Nieve",        5),
		new Task("Rune Dragons",        1,      0,      false,  "Nieve",        2),
		new Task("Scabarites",          1,      85,     false,  "Nieve",        4),
		new Task("Skeletal Wyverns",    72,     70,     false,  "Nieve",        5),
		new Task("Smoke Devils",        93,     85,     false,  "Nieve",        7),
		new Task("Spiritual Creatures", 63,     60,     false,  "Nieve",        6),
		new Task("Steel Dragons",       1,      85,     false,  "Nieve",        5),
		new Task("Suqahs",              1,      85,     false,  "Nieve",        8),
		new Task("Trolls",              1,      60,     false,  "Nieve",        6),
		new Task("Turoth",              55,     60,     false,  "Nieve",        3),
		new Task("TzHaar",              1,      0,      true,   "Nieve",        10)
	};
	private static Master nieve = new Master("Nieve", _nieve_tasks, 21.3, true);

	/* DURADEL */
	private static Task[] _duradel_tasks = {
		new Task("Aberrant Spectres",   60,     65,     false,  "Duradel",      7),
		new Task("Abyssal Demons",      85,     85,     false,  "Duradel",      12),
		new Task("Adamant Dragons",     1,      0,      false,  "Duradel",      2),
		new Task("Ankou",               1,      40,     false,  "Duradel",      5),
		new Task("Aviansies",           1,      0,      true,   "Duradel",      8),
		new Task("Black Demons",        1,      80,     false,  "Duradel",      8),
		new Task("Black Dragons",       1,      80,     false,  "Duradel",      9),
		new Task("Bloodveld",           50,     50,     false,  "Duradel",      8),
		new Task("Blue Dragons",        1,      65,     false,  "Duradel",      4),
		new Task("Bosses",              1,      0,      true,   "Duradel",      12),
		new Task("Cave Horrors",        58,     85,     false,  "Duradel",      4),
		new Task("Cave Kraken",         87,     80,     false,  "Duradel",      9),
		new Task("Dagannoth",           1,      75,     false,  "Duradel",      9),
		new Task("Dark Beasts",         90,     90,     false,  "Duradel",      11),
		new Task("Dust Devils",         65,     70,     false,  "Duradel",      5),
		new Task("Elves",               1,      70,     false,  "Duradel",      4),
		new Task("Fire Giants",         1,      65,     false,  "Duradel",      7),
		new Task("Fossil Wyverns",      66,     0,      true,   "Duradel",      5),
		new Task("Gargoyles",           75,     80,     false,  "Duradel",      8),
		new Task("Greater Demons",      1,      75,     false,  "Duradel",      9),
		new Task("Hellhounds",          1,      75,     false,  "Duradel",      10),
		new Task("Iron Dragons",        1,      80,     false,  "Duradel",      5),
		new Task("Kalphites",           1,      15,     false,  "Duradel",      9),
		new Task("Kurasks",             70,     65,     false,  "Duradel",      4),
		new Task("Lizardmen",           1,      0,      true,   "Duradel",      10),
		new Task("Mithril Dragons",     1,      0,      true,   "Duradel",      9),
		new Task("Mutated Zygomites",   57,     60,     false,  "Duradel",      2),
		new Task("Nechryael",           80,     85,     false,  "Duradel",      9),
		new Task("Red Dragons",         1,      0,      true,   "Duradel",      8),
		new Task("Rune Dragons",        1,      0,      false,  "Duradel",      2),
		new Task("Skeletal Wyverns",    72,     70,     false,  "Duradel",      7),
		new Task("Smoke Devils",        93,     85,     false,  "Duradel",      9),
		new Task("Spiritual Creatures", 63,     60,     false,  "Duradel",      7),
		new Task("Steel Dragons",       1,      85,     false,  "Duradel",      7),
		new Task("Suqahs",              1,      85,     false,  "Duradel",      8),
		new Task("Trolls",              1,      60,     false,  "Duradel",      6),
		new Task("TzHaar",              1,      0,      true,   "Duradel",      10),
		new Task("Waterfiends",         1,      75,     false,  "Duradel",      2)
	};
	private static Master duradel = new Master("Duradel", _duradel_tasks, 26.625, true);

	/* Finally, compile the list of information for user input. */
	static Master[] masters = {
		krystilia,
		turael,
		mazchna,
		vannaka,
		chaeldar,
		nieve,
		duradel
	};
}
