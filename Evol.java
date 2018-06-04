/* Uses an evolutionary algorithm to determine a user's optimal slayer master,
	and for that master, which tasks to block/skip.
 */
import java.util.Arrays;

public class Evol
{
	private static int scale = 10000;
	private static Constants.Master[] masters = Constants.masters;

	/* Info about the user: */
	private static int s_lvl;
	private static int cmb_lvl;
	private static int num_blocks;

	public static void main(String[] args)
	{
		int i, j, k, l, all;

		/* Used to keep track of the global state. */
		int best_master = -1;
		boolean cmb_app = false;
		double best = Integer.MIN_VALUE;
		boolean[] best_blocks = new boolean[1];

		/* A copy of tasks, but sorted in order of preference. */
		Constants.Task[] prefs;

		/* True if prefs[i] is an assignable task. */
		boolean[] poss;

		/* Used to do speculative blocks. */
		boolean swap = false;
		int change1 = 1, change2 = -1;
		int bans, next_bans;
		double spec, curr;
		boolean[] blocks;

		/* Used to print out a final list of skips. */
		boolean[] skips;
		double points;

		/* Get the user input. */
		user_in();

		/* For each enabled slayer master, find optimal setups, and keep track of the best. */
		for (i = 0; i < Constants.num_masters; i++) {
			if (!masters[i].enable)
				continue;

			all = masters[i].tasks.length;
			/* Generate a sorted list of tasks for the master. */
			prefs = new Constants.Task[all];
			System.arraycopy(masters[i].tasks, 0, prefs, 0, all);
			Arrays.sort(prefs, new Constants.sortPref());

			/* Find the optimal set of bans for non-combat appropriate. */

			/* Populate the possibility list. */
			poss = new boolean[all];
			for (j = 0; j < all; j++)
				poss[j] = s_lvl >= prefs[j].s_reqt && prefs[j].poss;

			bans = num_blocks;
			next_bans = num_blocks;
			blocks = new boolean[all];

			/* Find the optimal set of bans for no-restrictions tasks. */
			curr = score(masters[i].points, prefs, poss, blocks);
			while (change1 >= 0) {
				for (j = 0, change1 = -1; j < all; j++) {

					if (!poss[j])
						continue;

					if (blocks[j]) {
						blocks[j] = false;
						spec = score(masters[i].points, prefs, poss, blocks);
						if (spec > curr) {
							swap = false;
							change1 = j;
							curr = spec;
							next_bans = prefs[j].fblock ? bans : bans + 1;
						}
						blocks[j] = true;

					} else if (!blocks[j] && prefs[j].fblock) {
						blocks[j] = true;
						spec = score(masters[i].points, prefs, poss, blocks);
						if (spec > curr) {
							swap = false;
							change1 = j;
							curr = spec;
							next_bans = bans;
						}
						blocks[j] = false;

					} else if (!blocks[j] && !prefs[j].fblock && bans > 0) {
						blocks[j] = true;
						spec = score(masters[i].points, prefs, poss, blocks);
						if (spec >= curr) {
							swap = false;
							change1 = j;
							curr = spec;
							next_bans = bans - 1;
						}
						blocks[j] = false;

					} else if (!blocks[j] && !prefs[j].fblock && bans <= 0) {
						for (k = 0; k < all; k++) {
							if (blocks[k] && !prefs[k].fblock) {
								blocks[k] = false;
								blocks[j] = true;
								spec = score(masters[i].points, prefs, poss, blocks);
								if (spec > curr) {
									swap = true;
									change1 = j;
									change2 = k;
									curr = spec;
									next_bans = bans;
								}
								blocks[k] = true;
								blocks[j] = false;
							}
						}
					}
				}

				if (change1 >= 0) {
					blocks[change1] = !blocks[change1];
					bans = next_bans;
					if (swap)
						blocks[change2] = !blocks[change2];
				}
			}

			/* Compare the found extremum with the global best, and swap if needed. */
			if (curr > best) {
				best = curr;
				best_master = i;
				cmb_app = false;
				best_blocks = new boolean[all];
				System.arraycopy(blocks, 0, best_blocks, 0, all);
			}

			/* Find the optimal set of bans for combat-appropriate, if you can, and see if it's better. */
			if (masters[i].cmb_app) {

				/* Add in the combat level restriction. */
				for (j = 0; j < all; j++)
					poss[j] = poss[j] && cmb_lvl > prefs[j].cmb_rec;

				change1 = 1;
				bans = num_blocks;
				next_bans = num_blocks;
				blocks = new boolean[all];

				curr = score(masters[i].points, prefs, poss, blocks);
				while (change1 >= 0) {
					for (j = 0, change1 = -1; j < all; j++) {

						if (!poss[j])
							continue;

						if (blocks[j]) {
							blocks[j] = false;
							spec = score(masters[i].points, prefs, poss, blocks);
							if (spec > curr) {
								swap = false;
								change1 = j;
								curr = spec;
								next_bans = prefs[j].fblock ? bans : bans + 1;
							}
							blocks[j] = true;

						} else if (!blocks[j] && prefs[j].fblock) {
							blocks[j] = true;
							spec = score(masters[i].points, prefs, poss, blocks);
							if (spec > curr) {
								swap = false;
								change1 = j;
								curr = spec;
								next_bans = bans;
							}
							blocks[j] = false;

						} else if (!blocks[j] && !prefs[j].fblock && bans > 0) {
							blocks[j] = true;
							spec = score(masters[i].points, prefs, poss, blocks);
							if (spec >= curr) {
								swap = false;
								change1 = j;
								curr = spec;
								next_bans = bans - 1;
							}
							blocks[j] = false;

						} else if (!blocks[j] && !prefs[j].fblock && bans <= 0) {
							for (k = 0; k < all; k++) {
								if (blocks[k] && !prefs[k].fblock) {
									blocks[k] = false;
									blocks[j] = true;
									spec = score(masters[i].points, prefs, poss, blocks);
									if (spec > curr) {
										swap = true;
										change1 = j;
										change2 = k;
										curr = spec;
										next_bans = bans;
									}
									blocks[k] = true;
									blocks[j] = false;
								}
							}
						}
					}

					if (change1 >= 0) {
						blocks[change1] = !blocks[change1];
						bans = next_bans;
						if (swap)
							blocks[change2] = !blocks[change2];
					}
				}
				
				/* Compare the found extremum with the global best, and swap if needed. */
				if (curr > best) {
					best = curr;
					best_master = i;
					cmb_app = true;
					best_blocks = new boolean[all];
					System.arraycopy(blocks, 0, best_blocks, 0, all);
				}
			}
		}
		
		/* Now that we've found the best master, finalize the info. */
		all = masters[best_master].tasks.length;

		/* Generate a sorted list of tasks for the master. */
		prefs = new Constants.Task[all];
		System.arraycopy(masters[best_master].tasks, 0, prefs, 0, all);
		Arrays.sort(prefs, new Constants.sortPref());

		/* Populate the possibility list. */
		poss = new boolean[all];
		for (i = 0; i < all; i++)
			poss[i] = cmb_app ? s_lvl >= prefs[i].s_reqt && prefs[i].poss && cmb_lvl >= prefs[i].cmb_rec :
					    s_lvl >= prefs[i].s_reqt && prefs[i].poss;

		/* Generate the list of skips. */
		skips = new boolean[all];
		points = 0;
		change1 = 0;
		curr = 0;
		i = 0;
		while (!poss[i] || best_blocks[i])
			i++;
		j = 0;

		k = all - 1;
		while (!poss[k] || best_blocks[k])
			k--;
		l = scale * prefs[k].weight;

		/* Do tasks from the top down, and skip tasks from the bottom up until we meet. */
		while (i < k || (i == k && j < l)) {
			j++;
			points += masters[best_master].points;
			change1++;
			curr += prefs[i].pref;

			if (points >= 30) {
				points -= 30;
				l--;
			}

			if (j == scale * prefs[i].weight) {
				i++;
				while (!poss[i] || best_blocks[i])
					i++;
				j = 0;
			}

			if (l == 0) {
				skips[k] = true;
				k--;
				while (!poss[k] || best_blocks[k])
					k--;
				l = scale * prefs[k].weight;
			}
		}

		if (i == k && j == l) {
			change1++;
			curr += prefs[i].pref;
			j++;
		}

		/* Print out the final results. */
		System.out.println("\nScale: " + scale);

		System.out.println("\nMaster: " + masters[best_master].name);

		System.out.println(cmb_app ? "\nEnable Combat-level appropriate tasks." :
			"\nDisable Combat-level appropriate tasks.");

		System.out.println("\nUnlockables:");
		System.out.println("----------");
		for (i = 0; i < all; i++) {
			if (poss[i] && prefs[i].fblock)
				System.out.println(prefs[i].name + ":   " + (best_blocks[i] ? "Disabled" : "Enabled"));
		}

		System.out.println("\nBlocks:");
		System.out.println("-------");
		for (i = 0; i < all; i++) {
			if (!prefs[i].fblock && best_blocks[i])
				System.out.println(prefs[i].name);
		}

		System.out.println("\nSkips:");
		System.out.println("------");
		for (i = 0; i < all; i++) {
			if (skips[i])
				System.out.println(prefs[i].name);
		}

		System.out.printf("\nIn addition, you can skip %.1f%% of your " + prefs[k].name + " tasks.\n",
			(100 * (1 - (double)j / (scale * prefs[k].weight))));

		System.out.printf("\nAverage Task Quality: %.2f\n", (curr / change1));
	}

	private static double score(double pts, Constants.Task[] prefs, boolean[] poss, boolean[] blocks)
	{
		int i, j, k, l;
		int all = prefs.length, num_tasks = 0;
		double points = 0, score = 0;

		i = 0;
		while (!poss[i] || blocks[i])
			i++;
		j = 0;

		k = all - 1;
		while (!poss[k] || blocks[k])
			k--;
		l = scale * prefs[k].weight;

		/* Do tasks from the top down, while skipping tasks from the bottom up,
			until we reach the middle. */
		while (i < k || (i == k && j < l)) {
			j++;
			num_tasks++;
			score += prefs[i].pref;
			points += pts;

			if (points >= 30) {
				points -= 30;
				l--;
			}

			if (j == scale * prefs[i].weight) {
				i++;
				while (!poss[i] || blocks[i])
					i++;
				j = 0;
			}

			if (l == 0) {
				k--;
				while (!poss[k] || blocks[k])
					k--;
				l = scale * prefs[k].weight;
			}
		}

		/* Do the remaining task if there is ambiguity. */
		if (i == k && j == l) {
			num_tasks++;
			score += prefs[i].pref;
		}

		return score / num_tasks;
	}

	/* Takes in user-input for tasks. */
	private static void user_in()
	{
		s_lvl = 61;
		cmb_lvl = 82;
		num_blocks = 4;

		/* Krystilia */
		masters[0].user_in(false);
		/*
		masters[0].tasks[0].user_in(true, 5);   //Ankou
		masters[0].tasks[1].user_in(true, 5);   //Aviansies
		masters[0].tasks[2].user_in(true, 5);   //Bandits
		masters[0].tasks[3].user_in(true, 5);   //Bears
		masters[0].tasks[4].user_in(true, 5);   //Black Demons
		masters[0].tasks[5].user_in(true, 5);   //Black Dragons
		masters[0].tasks[6].user_in(true, 5);   //Bosses
		masters[0].tasks[7].user_in(true, 5);   //Chaos Druids
		masters[0].tasks[8].user_in(true, 5);   //Dark Warriors
		masters[0].tasks[9].user_in(true, 5);   //Earth Warriors
		masters[0].tasks[10].user_in(true, 5);  //Ents
		masters[0].tasks[11].user_in(true, 5);  //Fire Giants
		masters[0].tasks[12].user_in(true, 5);  //Greater Demons
		masters[0].tasks[13].user_in(true, 5);  //Green Dragons
		masters[0].tasks[14].user_in(true, 5);  //Hellhounds
		masters[0].tasks[15].user_in(true, 5);  //Ice Giants
		masters[0].tasks[16].user_in(true, 5);  //Ice Warriors
		masters[0].tasks[17].user_in(true, 5);  //Lava Dragons
		masters[0].tasks[18].user_in(true, 5);  //Lesser Demons
		masters[0].tasks[19].user_in(true, 5);  //Magic Axes
		masters[0].tasks[20].user_in(true, 5);  //Mammoths
		masters[0].tasks[21].user_in(true, 5);  //Revenants
		masters[0].tasks[22].user_in(true, 5);  //Rogues
		masters[0].tasks[23].user_in(true, 5);  //Scorpions
		masters[0].tasks[24].user_in(true, 5);  //Skeletons
		masters[0].tasks[25].user_in(true, 5);  //Spiders
		masters[0].tasks[26].user_in(true, 5);  //Spiritual Creatures
		*/

		/* Turael */
		masters[1].user_in(false);
		/*
		masters[1].tasks[0].user_in(true, 5);    //Banshees
		masters[1].tasks[1].user_in(true, 5);    //Bats
		masters[1].tasks[2].user_in(true, 5);    //Bears
		masters[1].tasks[3].user_in(true, 5);    //Birds
		masters[1].tasks[4].user_in(true, 5);    //Cave Bugs
		masters[1].tasks[5].user_in(true, 5);    //Cave Crawlers
		masters[1].tasks[6].user_in(true, 5);    //Cave Slimes
		masters[1].tasks[7].user_in(true, 5);    //Cows
		masters[1].tasks[8].user_in(true, 5);    //Crawling Hands
		masters[1].tasks[9].user_in(true, 5);    //Desert Lizards
		masters[1].tasks[10].user_in(true, 5);    //Dogs
		masters[1].tasks[11].user_in(true, 5);    //Dwarves
		masters[1].tasks[12].user_in(true, 5);    //Ghosts
		masters[1].tasks[13].user_in(true, 5);    //Goblins
		masters[1].tasks[14].user_in(true, 5);    //Icefiends
		masters[1].tasks[15].user_in(true, 5);    //Kalphites
		masters[1].tasks[16].user_in(true, 5);    //Minotaurs
		masters[1].tasks[17].user_in(true, 5);    //Monkeys
		masters[1].tasks[18].user_in(true, 5);    //Rats
		masters[1].tasks[19].user_in(true, 5);    //Scorpions
		masters[1].tasks[20].user_in(true, 5);    //Skeletons
		masters[1].tasks[21].user_in(true, 5);    //Spiders
		masters[1].tasks[22].user_in(true, 5);    //Wolves
		masters[1].tasks[23].user_in(true, 5);    //Zombies
		*/

		/* Mazchna */
		masters[2].user_in(false);
		/*
		masters[2].tasks[0].user_in(true, 5);   //Banshees
		masters[2].tasks[1].user_in(true, 5);   //Bats
		masters[2].tasks[2].user_in(true, 5);   //Bears
		masters[2].tasks[3].user_in(true, 5);   //Catablepons
		masters[2].tasks[4].user_in(true, 5);   //Cave Bugs
		masters[2].tasks[5].user_in(true, 5);   //Cave Crawlers
		masters[2].tasks[6].user_in(true, 5);   //Cave Slimes
		masters[2].tasks[7].user_in(true, 5);   //Cockatrice
		masters[2].tasks[8].user_in(true, 5);   //Crawling Hands
		masters[2].tasks[9].user_in(true, 5);   //Desert Lizards
		masters[2].tasks[10].user_in(true, 5);   //Dogs
		masters[2].tasks[11].user_in(true, 5);   //Earth Warriors
		masters[2].tasks[12].user_in(true, 5);   //Flesh Crawlers
		masters[2].tasks[13].user_in(true, 5);   //Ghosts
		masters[2].tasks[14].user_in(true, 5);   //Ghouls
		masters[2].tasks[15].user_in(true, 5);   //Hill Giants
		masters[2].tasks[16].user_in(true, 5);   //Hobgoblins
		masters[2].tasks[17].user_in(true, 5);   //Ice Warriors
		masters[2].tasks[18].user_in(true, 5);   //Kalphites
		masters[2].tasks[19].user_in(true, 5);   //Killerwatts
		masters[2].tasks[20].user_in(true, 5);   //Mogres
		masters[2].tasks[21].user_in(true, 5);   //Pyrefiends
		masters[2].tasks[22].user_in(true, 5);   //Rockslugs
		masters[2].tasks[23].user_in(true, 5);   //Scorpions
		masters[2].tasks[24].user_in(true, 5);   //Shades
		masters[2].tasks[25].user_in(true, 5);   //Skeletons
		masters[2].tasks[26].user_in(true, 5);   //Vampyres
		masters[2].tasks[27].user_in(true, 5);   //Wall Beasts
		masters[2].tasks[28].user_in(true, 5);   //Wolves
		masters[2].tasks[29].user_in(true, 5);   //Zombies
		*/

		/* Vannaka */
		masters[3].user_in(false);
		/*
		masters[3].tasks[0].user_in(true, 5);    //Aberrant Spectres
		masters[3].tasks[1].user_in(true, 5);    //Abyssal Demons
		masters[3].tasks[2].user_in(true, 5);    //Ankou:
		masters[3].tasks[3].user_in(true, 5);    //Banshees
		masters[3].tasks[4].user_in(true, 5);    //Basilisks
		masters[3].tasks[5].user_in(true, 5);    //Bloodveld
		masters[3].tasks[6].user_in(true, 5);    //Blue Dragons:
		masters[3].tasks[7].user_in(true, 5);    //Brine Rats
		masters[3].tasks[8].user_in(true, 5);    //Bronze Dragons:
		masters[3].tasks[9].user_in(true, 5);    //Cave Bugs
		masters[3].tasks[10].user_in(true, 5);   //Cave Crawlers
		masters[3].tasks[11].user_in(true, 5);   //Cave Slimes
		masters[3].tasks[12].user_in(true, 5);   //Cockatrice
		masters[3].tasks[13].user_in(true, 5);   //Crawling Hands
		masters[3].tasks[14].user_in(true, 5);   //Crocodile:
		masters[3].tasks[15].user_in(true, 5);   //Dagannoth
		masters[3].tasks[16].user_in(true, 5);   //Desert Lizards
		masters[3].tasks[17].user_in(true, 5);   //Dust Devils
		masters[3].tasks[18].user_in(true, 5);   //Earth Warriors:
		masters[3].tasks[19].user_in(true, 5);   //Elves
		masters[3].tasks[20].user_in(true, 5);   //Fever Spiders
		masters[3].tasks[21].user_in(true, 5);   //Fire Giants:
		masters[3].tasks[22].user_in(true, 5);   //Gargoyles
		masters[3].tasks[23].user_in(true, 5);   //Ghouls
		masters[3].tasks[24].user_in(true, 5);   //Green Dragons:
		masters[3].tasks[25].user_in(true, 5);   //Harpie Bug Swarms
		masters[3].tasks[26].user_in(true, 5);   //Hellhounds:
		masters[3].tasks[27].user_in(true, 5);   //Hill Giants:
		masters[3].tasks[28].user_in(true, 5);   //Hobgoblins:
		masters[3].tasks[29].user_in(true, 5);   //Ice Giants:
		masters[3].tasks[30].user_in(true, 5);   //Ice Warriors:
		masters[3].tasks[31].user_in(true, 5);   //Infernal Mages
		masters[3].tasks[32].user_in(true, 5);   //Jellies
		masters[3].tasks[33].user_in(true, 5);   //Jungle Horrors
		masters[3].tasks[34].user_in(true, 5);   //Kalphites:
		masters[3].tasks[35].user_in(true, 5);   //Killerwatts
		masters[3].tasks[36].user_in(true, 5);   //Kurasks
		masters[3].tasks[37].user_in(true, 5);   //Lesser Demons:
		masters[3].tasks[38].user_in(true, 5);   //Shades:
		masters[3].tasks[39].user_in(true, 5);   //Mogres
		masters[3].tasks[40].user_in(true, 5);   //Molannisks
		masters[3].tasks[41].user_in(true, 5);   //Moss Giants:
		masters[3].tasks[42].user_in(true, 5);   //Nechryael
		masters[3].tasks[43].user_in(true, 5);   //Ogres:
		masters[3].tasks[44].user_in(true, 5);   //Otherworldly Beings
		masters[3].tasks[45].user_in(true, 5);   //Pyrefiends
		masters[3].tasks[46].user_in(true, 5);   //Rockslugs
		masters[3].tasks[47].user_in(true, 5);   //Sea Snakes:
		masters[3].tasks[48].user_in(true, 5);   //Shadow Warriors
		masters[3].tasks[49].user_in(true, 5);   //Spiritual Creatures
		masters[3].tasks[50].user_in(true, 5);   //Terror Dogs
		masters[3].tasks[51].user_in(true, 5);   //Trolls:
		masters[3].tasks[52].user_in(true, 5);   //Turoth
		masters[3].tasks[53].user_in(true, 5);   //Vampyres
		masters[3].tasks[54].user_in(true, 5);   //Wall Beasts
		masters[3].tasks[55].user_in(true, 5);   //Werewolves
		*/

		/* Chaeldar */
		masters[4].user_in(true);
		masters[4].tasks[0].user_in(true, 3);    //Aberrant Spectres
		masters[4].tasks[1].user_in(true, 0);    //Abyssal Demons
		masters[4].tasks[2].user_in(true, 3);    //Aviansies
		masters[4].tasks[3].user_in(true, 1);    //Banshees
		masters[4].tasks[4].user_in(true, 1);    //Basilisks
		masters[4].tasks[5].user_in(true, 1);    //Black Demons
		masters[4].tasks[6].user_in(true, 1);    //Bloodveld
		masters[4].tasks[7].user_in(true, 3);    //Blue Dragons
		masters[4].tasks[8].user_in(false, 1);    //Brine Rats
		masters[4].tasks[9].user_in(true, 1);    //Bronze Dragons
		masters[4].tasks[10].user_in(true, 1);   //Cave Crawlers
		masters[4].tasks[11].user_in(true, 2);   //Cave Horrors
		masters[4].tasks[12].user_in(true, 0);   //Cave Kraken
		masters[4].tasks[13].user_in(true, 1);   //Cave Slimes
		masters[4].tasks[14].user_in(true, 1);   //Cockatrice
		masters[4].tasks[15].user_in(true, 2);   //Dagannoth
		masters[4].tasks[16].user_in(true, 1);   //Desert Lizards
		masters[4].tasks[17].user_in(true, 0);   //Dust Devils
		masters[4].tasks[18].user_in(true, 2);   //Elves
		masters[4].tasks[19].user_in(true, 1);   //Fever Spiders
		masters[4].tasks[20].user_in(true, 1);   //Fire Giants
		masters[4].tasks[21].user_in(true, 0);   //Fossil Island Wyverns
		masters[4].tasks[22].user_in(true, 0);   //Gargoyles
		masters[4].tasks[23].user_in(true, 2);   //Greater Demons
		masters[4].tasks[24].user_in(true, 1);   //Harpie Bug Swarms
		masters[4].tasks[25].user_in(true, 2);   //Hellhounds
		masters[4].tasks[26].user_in(true, 1);   //Infernal Mages
		masters[4].tasks[27].user_in(true, 2);   //Iron Dragons
		masters[4].tasks[28].user_in(true, 1);   //Jellies
		masters[4].tasks[29].user_in(true, 1);   //Jungle Horrors
		masters[4].tasks[30].user_in(true, 2);   //Kalphites
		masters[4].tasks[31].user_in(true, 0);   //Kurasks
		masters[4].tasks[32].user_in(true, 1);   //Lesser Demons
		masters[4].tasks[33].user_in(true, 3);   //Lizardmen
		masters[4].tasks[34].user_in(true, 1);   //Mogres
		masters[4].tasks[35].user_in(true, 1);   //Molanisks
		masters[4].tasks[36].user_in(true, 1);   //Mutated Zygomites
		masters[4].tasks[37].user_in(true, 0);   //Nechryael
		masters[4].tasks[38].user_in(true, 1);   //Pyrefiends
		masters[4].tasks[39].user_in(true, 1);   //Rockslugs
		masters[4].tasks[40].user_in(true, 1);   //Shadow Warriors
		masters[4].tasks[41].user_in(true, 0);   //Skeletal Wyverns
		masters[4].tasks[42].user_in(true, 0);   //Spiritual Creatures
		masters[4].tasks[43].user_in(true, 3);   //Steel Dragons
		masters[4].tasks[44].user_in(true, 1);   //Trolls
		masters[4].tasks[45].user_in(true, 2);   //Turoth
		masters[4].tasks[46].user_in(true, 3);   //TzHaar
		masters[4].tasks[47].user_in(true, 1);   //Wall Beast

		/* Nieve */
		masters[5].user_in(false);
		/*
		masters[5].tasks[0].user_in(true, 100);    //Aberrant Spectres
		masters[5].tasks[1].user_in(true, 100);    //Abyssal Demons
		masters[5].tasks[2].user_in(true, 100);    //Adamant Dragons
		masters[5].tasks[3].user_in(true, 100);    //Ankou
		masters[5].tasks[4].user_in(true, 100);    //Aviansies
		masters[5].tasks[5].user_in(true, 100);    //Black Demons
		masters[5].tasks[6].user_in(true, 100);    //Black Dragons
		masters[5].tasks[7].user_in(true, 100);    //Bloodveld
		masters[5].tasks[8].user_in(true, 100);    //Blue Dragons
		masters[5].tasks[9].user_in(true, 100);    //Bosses
		masters[5].tasks[10].user_in(true, 100);   //Brine Rats
		masters[5].tasks[11].user_in(true, 100);   //Cave Horrors
		masters[5].tasks[12].user_in(true, 100);   //Cave Kraken
		masters[5].tasks[13].user_in(true, 100);   //Dagannoth
		masters[5].tasks[14].user_in(true, 5);   //Dark Beasts
		masters[5].tasks[15].user_in(true, 5);   //Dust Devils
		masters[5].tasks[16].user_in(true, 100);   //Elves
		masters[5].tasks[17].user_in(true, 5);   //Fire Giants
		masters[5].tasks[18].user_in(true, 5);   //Fossil Island Wyverns
		masters[5].tasks[19].user_in(true, 100);   //Gargoyles
		masters[5].tasks[20].user_in(true, 100);   //Greater Demons
		masters[5].tasks[21].user_in(true, 5);   //Hellhounds
		masters[5].tasks[22].user_in(true, 5);   //Iron Dragons
		masters[5].tasks[23].user_in(true, 100);   //Kalphites
		masters[5].tasks[24].user_in(true, 5);   //Kurasks
		masters[5].tasks[25].user_in(true, 100);   //Lizardmen
		masters[5].tasks[26].user_in(true, 5);   //Mithril Dragons
		masters[5].tasks[27].user_in(true, 5);   //Mutated Zygomites
		masters[5].tasks[28].user_in(true, 100);   //Nechryael
		masters[5].tasks[29].user_in(true, 100);   //Red Dragons
		masters[5].tasks[30].user_in(true, 5);   //Rune Dragons
		masters[5].tasks[31].user_in(true, 100);   //Scabarites
		masters[5].tasks[32].user_in(true, 5);   //Skeletal Wyverns
		masters[5].tasks[33].user_in(true, 5);   //Smoke Devils
		masters[5].tasks[34].user_in(true, 5);   //Spiritual Creatures
		masters[5].tasks[35].user_in(true, 5);   //Steel Dragons
		masters[5].tasks[36].user_in(true, 100);   //Suqahs
		masters[5].tasks[37].user_in(true, 5);   //Trolls
		masters[5].tasks[38].user_in(true, 5);   //Turoth
		masters[5].tasks[39].user_in(true, 100);   //TzHaar
		*/

		/* Duradel */
		masters[6].user_in(false);
		/*
		masters[6].tasks[0].user_in(true, 15);    //Aberrant Spectres
		masters[6].tasks[1].user_in(true, 16);    //Abyssal Demons
		masters[6].tasks[2].user_in(true, 2);    //Adamant Dragons
		masters[6].tasks[3].user_in(true, 21);    //Ankou
		masters[6].tasks[4].user_in(true, 1);    //Aviansies
		masters[6].tasks[5].user_in(true, 3);    //Black Demons
		masters[6].tasks[6].user_in(true, 18);    //Black Dragons
		masters[6].tasks[7].user_in(true, 14);    //Bloodveld
		masters[6].tasks[8].user_in(true, 7);    //Blue Dragons
		masters[6].tasks[9].user_in(true, 0);    //Bosses
		masters[6].tasks[10].user_in(true, 8);   //Cave Horrors
		masters[6].tasks[11].user_in(true, 11);   //Cave Kraken
		masters[6].tasks[12].user_in(true, 24);   //Dagannoth
		masters[6].tasks[13].user_in(true, 6);   //Dark Beasts
		masters[6].tasks[14].user_in(true, 22);   //Dust Devils
		masters[6].tasks[15].user_in(true, 4);   //Elves
		masters[6].tasks[16].user_in(true, 20);   //Fire Giants
		masters[6].tasks[17].user_in(true, 0);   //Fossil Island Wyverns
		masters[6].tasks[18].user_in(true, 17);   //Gargoyles
		masters[6].tasks[19].user_in(true, 3);   //Greater Demons
		masters[6].tasks[20].user_in(true, 19);   //Hellhounds
		masters[6].tasks[21].user_in(true, 0);   //Iron Dragons
		masters[6].tasks[22].user_in(true, 5);   //Kalphites
		masters[6].tasks[23].user_in(true, 9);   //Kurasks
		masters[6].tasks[24].user_in(true, 3);   //Lizardmen
		masters[6].tasks[25].user_in(true, 0);   //Mithril Dragons
		masters[6].tasks[26].user_in(true, 5);   //Mutated Zygomites
		masters[6].tasks[27].user_in(true, 23);   //Nechryael
		masters[6].tasks[28].user_in(true, 0);   //Red Dragons
		masters[6].tasks[29].user_in(true, 0);   //Rune Dragons
		masters[6].tasks[30].user_in(true, 10);   //Skeletal Wyverns
		masters[6].tasks[31].user_in(true, 7);   //Smoke Devils
		masters[6].tasks[32].user_in(true, 1);   //Spiritual Creatures
		masters[6].tasks[33].user_in(true, 0);   //Steel Dragons
		masters[6].tasks[34].user_in(true, 13);   //Suqahs
		masters[6].tasks[35].user_in(true, 12);   //Trolls
		masters[6].tasks[36].user_in(true, 25);   //TzHaar
		masters[6].tasks[37].user_in(true, 5);   //Waterfiends
		*/

		}
}
