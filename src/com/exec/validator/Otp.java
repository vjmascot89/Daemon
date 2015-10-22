package com.exec.validator;

/* Class for implementing OTP (aka s/key) one-time password calculation
 * using the accompanying md class for md4 (and hopefully md5 eventually) 
 * based key calculation.
 *
 * The constructor is used to set the challenge info and passphrase,
 * and the calc() method calculates the otp. The results can either
 * be retrieved using the tolong() method, which gives you the 64 bits
 * "folded" hash in a single word, or else as a String of otp "words"
 * via toString().
 *
 * Cripes this is slow. How can we make it faster? 
 *
 * Copyright 1996 Harry Mantakos, harry@cs.umd.edu
 */

class otp {
	int seq;
	String seed, passphrase;
	byte hash[];
	int sha;
	final static byte MD4 = 4;
	final static byte MD5 = 5;

	otp(int n, String s, String p, int hashalg) {
		this.seq = n;
		this.seed = s;
		this.passphrase = p;
		this.sha = hashalg;
	}

	void calc() {
		if (this.sha == MD5) {
			this.md5calc();
		} else {
			this.md4calc();
		}
	}

	void md4calc() {
		int tmpseq = this.seq;
		md4 mdc;

		mdc = new md4(this.seed + this.passphrase);
		mdc.calc();
		this.hash = otpfoldregs(mdc.getregs());
		while (tmpseq > 0) {
			mdc = new md4(hash);
			mdc.calc();
			this.hash = otpfoldregs(mdc.getregs());
			tmpseq--;
		}
	}

	void md5calc() {
		int tmpseq = this.seq;
		md5 mdc;

		mdc = new md5(this.seed + this.passphrase);
		mdc.calc();
		this.hash = otpfoldregs(mdc.getregs());
		while (tmpseq > 0) {
			mdc = new md5(hash);
			mdc.calc();
			this.hash = otpfoldregs(mdc.getregs());
			tmpseq--;
		}
	}

	static byte[] otpfoldregs(int regs[]) {
		int ac, bd, i;
		byte fold[] = new byte[8];

		ac = regs[0] ^ regs[2];
		bd = regs[1] ^ regs[3];
		for (i = 0; i < 4; i++) {
			fold[i] = (byte) (ac & 0xff);
			ac >>= 8;
		}
		for (i = 4; i < 8; i++) {
			fold[i] = (byte) (bd & 0xff);
			bd >>= 8;
		}
		return fold;
	}

	long tolong() {
		long wi;
		int i;

		wi = 0;
		for (i = 0; i < 8; i++) {
			wi <<= 8;
			wi |= (this.hash[i] & 0xff);
		}
		return wi;
	}

	public String toString() {
		long wi, tmplong;
		String tmpstr;
		int i, j;
		byte parity;

		wi = this.tolong();
		tmplong = wi;
		tmpstr = "";
		parity = 0;
		for (i = 0; i < 64; i += 2) {
			parity += tmplong & 0x3;
			tmplong >>= 2;
		}
		for (i = 4; i >= 0; i--) {
			tmpstr += btoe((int) ((wi >> (i * 11 + 9)) & 0x7ff)) + " ";
		}
		tmpstr += btoe((int) ((wi << 2) & 0x7fc) | (parity & 0x03));
		return tmpstr;
	}

	public static String btoe(int index) {
		if (index < words.length) {
			return words[index];
		} else {
			return "bogus";
		}
	}

	static String words[] = { "A", "ABE", "ACE", "ACT", "AD", "ADA", "ADD",
			"AGO", "AID", "AIM", "AIR", "ALL", "ALP", "AM", "AMY", "AN", "ANA",
			"AND", "ANN", "ANT", "ANY", "APE", "APS", "APT", "ARC", "ARE",
			"ARK", "ARM", "ART", "AS", "ASH", "ASK", "AT", "ATE", "AUG", "AUK",
			"AVE", "AWE", "AWK", "AWL", "AWN", "AX", "AYE", "BAD", "BAG",
			"BAH", "BAM", "BAN", "BAR", "BAT", "BAY", "BE", "BED", "BEE",
			"BEG", "BEN", "BET", "BEY", "BIB", "BID", "BIG", "BIN", "BIT",
			"BOB", "BOG", "BON", "BOO", "BOP", "BOW", "BOY", "BUB", "BUD",
			"BUG", "BUM", "BUN", "BUS", "BUT", "BUY", "BY", "BYE", "CAB",
			"CAL", "CAM", "CAN", "CAP", "CAR", "CAT", "CAW", "COD", "COG",
			"COL", "CON", "COO", "COP", "COT", "COW", "COY", "CRY", "CUB",
			"CUE", "CUP", "CUR", "CUT", "DAB", "DAD", "DAM", "DAN", "DAR",
			"DAY", "DEE", "DEL", "DEN", "DES", "DEW", "DID", "DIE", "DIG",
			"DIN", "DIP", "DO", "DOE", "DOG", "DON", "DOT", "DOW", "DRY",
			"DUB", "DUD", "DUE", "DUG", "DUN", "EAR", "EAT", "ED", "EEL",
			"EGG", "EGO", "ELI", "ELK", "ELM", "ELY", "EM", "END", "EST",
			"ETC", "EVA", "EVE", "EWE", "EYE", "FAD", "FAN", "FAR", "FAT",
			"FAY", "FED", "FEE", "FEW", "FIB", "FIG", "FIN", "FIR", "FIT",
			"FLO", "FLY", "FOE", "FOG", "FOR", "FRY", "FUM", "FUN", "FUR",
			"GAB", "GAD", "GAG", "GAL", "GAM", "GAP", "GAS", "GAY", "GEE",
			"GEL", "GEM", "GET", "GIG", "GIL", "GIN", "GO", "GOT", "GUM",
			"GUN", "GUS", "GUT", "GUY", "GYM", "GYP", "HA", "HAD", "HAL",
			"HAM", "HAN", "HAP", "HAS", "HAT", "HAW", "HAY", "HE", "HEM",
			"HEN", "HER", "HEW", "HEY", "HI", "HID", "HIM", "HIP", "HIS",
			"HIT", "HO", "HOB", "HOC", "HOE", "HOG", "HOP", "HOT", "HOW",
			"HUB", "HUE", "HUG", "HUH", "HUM", "HUT", "I", "ICY", "IDA", "IF",
			"IKE", "ILL", "INK", "INN", "IO", "ION", "IQ", "IRA", "IRE", "IRK",
			"IS", "IT", "ITS", "IVY", "JAB", "JAG", "JAM", "JAN", "JAR", "JAW",
			"JAY", "JET", "JIG", "JIM", "JO", "JOB", "JOE", "JOG", "JOT",
			"JOY", "JUG", "JUT", "KAY", "KEG", "KEN", "KEY", "KID", "KIM",
			"KIN", "KIT", "LA", "LAB", "LAC", "LAD", "LAG", "LAM", "LAP",
			"LAW", "LAY", "LEA", "LED", "LEE", "LEG", "LEN", "LEO", "LET",
			"LEW", "LID", "LIE", "LIN", "LIP", "LIT", "LO", "LOB", "LOG",
			"LOP", "LOS", "LOT", "LOU", "LOW", "LOY", "LUG", "LYE", "MA",
			"MAC", "MAD", "MAE", "MAN", "MAO", "MAP", "MAT", "MAW", "MAY",
			"ME", "MEG", "MEL", "MEN", "MET", "MEW", "MID", "MIN", "MIT",
			"MOB", "MOD", "MOE", "MOO", "MOP", "MOS", "MOT", "MOW", "MUD",
			"MUG", "MUM", "MY", "NAB", "NAG", "NAN", "NAP", "NAT", "NAY", "NE",
			"NED", "NEE", "NET", "NEW", "NIB", "NIL", "NIP", "NIT", "NO",
			"NOB", "NOD", "NON", "NOR", "NOT", "NOV", "NOW", "NU", "NUN",
			"NUT", "O", "OAF", "OAK", "OAR", "OAT", "ODD", "ODE", "OF", "OFF",
			"OFT", "OH", "OIL", "OK", "OLD", "ON", "ONE", "OR", "ORB", "ORE",
			"ORR", "OS", "OTT", "OUR", "OUT", "OVA", "OW", "OWE", "OWL", "OWN",
			"OX", "PA", "PAD", "PAL", "PAM", "PAN", "PAP", "PAR", "PAT", "PAW",
			"PAY", "PEA", "PEG", "PEN", "PEP", "PER", "PET", "PEW", "PHI",
			"PI", "PIE", "PIN", "PIT", "PLY", "PO", "POD", "POE", "POP", "POT",
			"POW", "PRO", "PRY", "PUB", "PUG", "PUN", "PUP", "PUT", "QUO",
			"RAG", "RAM", "RAN", "RAP", "RAT", "RAW", "RAY", "REB", "RED",
			"REP", "RET", "RIB", "RID", "RIG", "RIM", "RIO", "RIP", "ROB",
			"ROD", "ROE", "RON", "ROT", "ROW", "ROY", "RUB", "RUE", "RUG",
			"RUM", "RUN", "RYE", "SAC", "SAD", "SAG", "SAL", "SAM", "SAN",
			"SAP", "SAT", "SAW", "SAY", "SEA", "SEC", "SEE", "SEN", "SET",
			"SEW", "SHE", "SHY", "SIN", "SIP", "SIR", "SIS", "SIT", "SKI",
			"SKY", "SLY", "SO", "SOB", "SOD", "SON", "SOP", "SOW", "SOY",
			"SPA", "SPY", "SUB", "SUD", "SUE", "SUM", "SUN", "SUP", "TAB",
			"TAD", "TAG", "TAN", "TAP", "TAR", "TEA", "TED", "TEE", "TEN",
			"THE", "THY", "TIC", "TIE", "TIM", "TIN", "TIP", "TO", "TOE",
			"TOG", "TOM", "TON", "TOO", "TOP", "TOW", "TOY", "TRY", "TUB",
			"TUG", "TUM", "TUN", "TWO", "UN", "UP", "US", "USE", "VAN", "VAT",
			"VET", "VIE", "WAD", "WAG", "WAR", "WAS", "WAY", "WE", "WEB",
			"WED", "WEE", "WET", "WHO", "WHY", "WIN", "WIT", "WOK", "WON",
			"WOO", "WOW", "WRY", "WU", "YAM", "YAP", "YAW", "YE", "YEA", "YES",
			"YET", "YOU", "ABED", "ABEL", "ABET", "ABLE", "ABUT", "ACHE",
			"ACID", "ACME", "ACRE", "ACTA", "ACTS", "ADAM", "ADDS", "ADEN",
			"AFAR", "AFRO", "AGEE", "AHEM", "AHOY", "AIDA", "AIDE", "AIDS",
			"AIRY", "AJAR", "AKIN", "ALAN", "ALEC", "ALGA", "ALIA", "ALLY",
			"ALMA", "ALOE", "ALSO", "ALTO", "ALUM", "ALVA", "AMEN", "AMES",
			"AMID", "AMMO", "AMOK", "AMOS", "AMRA", "ANDY", "ANEW", "ANNA",
			"ANNE", "ANTE", "ANTI", "AQUA", "ARAB", "ARCH", "AREA", "ARGO",
			"ARID", "ARMY", "ARTS", "ARTY", "ASIA", "ASKS", "ATOM", "AUNT",
			"AURA", "AUTO", "AVER", "AVID", "AVIS", "AVON", "AVOW", "AWAY",
			"AWRY", "BABE", "BABY", "BACH", "BACK", "BADE", "BAIL", "BAIT",
			"BAKE", "BALD", "BALE", "BALI", "BALK", "BALL", "BALM", "BAND",
			"BANE", "BANG", "BANK", "BARB", "BARD", "BARE", "BARK", "BARN",
			"BARR", "BASE", "BASH", "BASK", "BASS", "BATE", "BATH", "BAWD",
			"BAWL", "BEAD", "BEAK", "BEAM", "BEAN", "BEAR", "BEAT", "BEAU",
			"BECK", "BEEF", "BEEN", "BEER", "BEET", "BELA", "BELL", "BELT",
			"BEND", "BENT", "BERG", "BERN", "BERT", "BESS", "BEST", "BETA",
			"BETH", "BHOY", "BIAS", "BIDE", "BIEN", "BILE", "BILK", "BILL",
			"BIND", "BING", "BIRD", "BITE", "BITS", "BLAB", "BLAT", "BLED",
			"BLEW", "BLOB", "BLOC", "BLOT", "BLOW", "BLUE", "BLUM", "BLUR",
			"BOAR", "BOAT", "BOCA", "BOCK", "BODE", "BODY", "BOGY", "BOHR",
			"BOIL", "BOLD", "BOLO", "BOLT", "BOMB", "BONA", "BOND", "BONE",
			"BONG", "BONN", "BONY", "BOOK", "BOOM", "BOON", "BOOT", "BORE",
			"BORG", "BORN", "BOSE", "BOSS", "BOTH", "BOUT", "BOWL", "BOYD",
			"BRAD", "BRAE", "BRAG", "BRAN", "BRAY", "BRED", "BREW", "BRIG",
			"BRIM", "BROW", "BUCK", "BUDD", "BUFF", "BULB", "BULK", "BULL",
			"BUNK", "BUNT", "BUOY", "BURG", "BURL", "BURN", "BURR", "BURT",
			"BURY", "BUSH", "BUSS", "BUST", "BUSY", "BYTE", "CADY", "CAFE",
			"CAGE", "CAIN", "CAKE", "CALF", "CALL", "CALM", "CAME", "CANE",
			"CANT", "CARD", "CARE", "CARL", "CARR", "CART", "CASE", "CASH",
			"CASK", "CAST", "CAVE", "CEIL", "CELL", "CENT", "CERN", "CHAD",
			"CHAR", "CHAT", "CHAW", "CHEF", "CHEN", "CHEW", "CHIC", "CHIN",
			"CHOU", "CHOW", "CHUB", "CHUG", "CHUM", "CITE", "CITY", "CLAD",
			"CLAM", "CLAN", "CLAW", "CLAY", "CLOD", "CLOG", "CLOT", "CLUB",
			"CLUE", "COAL", "COAT", "COCA", "COCK", "COCO", "CODA", "CODE",
			"CODY", "COED", "COIL", "COIN", "COKE", "COLA", "COLD", "COLT",
			"COMA", "COMB", "COME", "COOK", "COOL", "COON", "COOT", "CORD",
			"CORE", "CORK", "CORN", "COST", "COVE", "COWL", "CRAB", "CRAG",
			"CRAM", "CRAY", "CREW", "CRIB", "CROW", "CRUD", "CUBA", "CUBE",
			"CUFF", "CULL", "CULT", "CUNY", "CURB", "CURD", "CURE", "CURL",
			"CURT", "CUTS", "DADE", "DALE", "DAME", "DANA", "DANE", "DANG",
			"DANK", "DARE", "DARK", "DARN", "DART", "DASH", "DATA", "DATE",
			"DAVE", "DAVY", "DAWN", "DAYS", "DEAD", "DEAF", "DEAL", "DEAN",
			"DEAR", "DEBT", "DECK", "DEED", "DEEM", "DEER", "DEFT", "DEFY",
			"DELL", "DENT", "DENY", "DESK", "DIAL", "DICE", "DIED", "DIET",
			"DIME", "DINE", "DING", "DINT", "DIRE", "DIRT", "DISC", "DISH",
			"DISK", "DIVE", "DOCK", "DOES", "DOLE", "DOLL", "DOLT", "DOME",
			"DONE", "DOOM", "DOOR", "DORA", "DOSE", "DOTE", "DOUG", "DOUR",
			"DOVE", "DOWN", "DRAB", "DRAG", "DRAM", "DRAW", "DREW", "DRUB",
			"DRUG", "DRUM", "DUAL", "DUCK", "DUCT", "DUEL", "DUET", "DUKE",
			"DULL", "DUMB", "DUNE", "DUNK", "DUSK", "DUST", "DUTY", "EACH",
			"EARL", "EARN", "EASE", "EAST", "EASY", "EBEN", "ECHO", "EDDY",
			"EDEN", "EDGE", "EDGY", "EDIT", "EDNA", "EGAN", "ELAN", "ELBA",
			"ELLA", "ELSE", "EMIL", "EMIT", "EMMA", "ENDS", "ERIC", "EROS",
			"EVEN", "EVER", "EVIL", "EYED", "FACE", "FACT", "FADE", "FAIL",
			"FAIN", "FAIR", "FAKE", "FALL", "FAME", "FANG", "FARM", "FAST",
			"FATE", "FAWN", "FEAR", "FEAT", "FEED", "FEEL", "FEET", "FELL",
			"FELT", "FEND", "FERN", "FEST", "FEUD", "FIEF", "FIGS", "FILE",
			"FILL", "FILM", "FIND", "FINE", "FINK", "FIRE", "FIRM", "FISH",
			"FISK", "FIST", "FITS", "FIVE", "FLAG", "FLAK", "FLAM", "FLAT",
			"FLAW", "FLEA", "FLED", "FLEW", "FLIT", "FLOC", "FLOG", "FLOW",
			"FLUB", "FLUE", "FOAL", "FOAM", "FOGY", "FOIL", "FOLD", "FOLK",
			"FOND", "FONT", "FOOD", "FOOL", "FOOT", "FORD", "FORE", "FORK",
			"FORM", "FORT", "FOSS", "FOUL", "FOUR", "FOWL", "FRAU", "FRAY",
			"FRED", "FREE", "FRET", "FREY", "FROG", "FROM", "FUEL", "FULL",
			"FUME", "FUND", "FUNK", "FURY", "FUSE", "FUSS", "GAFF", "GAGE",
			"GAIL", "GAIN", "GAIT", "GALA", "GALE", "GALL", "GALT", "GAME",
			"GANG", "GARB", "GARY", "GASH", "GATE", "GAUL", "GAUR", "GAVE",
			"GAWK", "GEAR", "GELD", "GENE", "GENT", "GERM", "GETS", "GIBE",
			"GIFT", "GILD", "GILL", "GILT", "GINA", "GIRD", "GIRL", "GIST",
			"GIVE", "GLAD", "GLEE", "GLEN", "GLIB", "GLOB", "GLOM", "GLOW",
			"GLUE", "GLUM", "GLUT", "GOAD", "GOAL", "GOAT", "GOER", "GOES",
			"GOLD", "GOLF", "GONE", "GONG", "GOOD", "GOOF", "GORE", "GORY",
			"GOSH", "GOUT", "GOWN", "GRAB", "GRAD", "GRAY", "GREG", "GREW",
			"GREY", "GRID", "GRIM", "GRIN", "GRIT", "GROW", "GRUB", "GULF",
			"GULL", "GUNK", "GURU", "GUSH", "GUST", "GWEN", "GWYN", "HAAG",
			"HAAS", "HACK", "HAIL", "HAIR", "HALE", "HALF", "HALL", "HALO",
			"HALT", "HAND", "HANG", "HANK", "HANS", "HARD", "HARK", "HARM",
			"HART", "HASH", "HAST", "HATE", "HATH", "HAUL", "HAVE", "HAWK",
			"HAYS", "HEAD", "HEAL", "HEAR", "HEAT", "HEBE", "HECK", "HEED",
			"HEEL", "HEFT", "HELD", "HELL", "HELM", "HERB", "HERD", "HERE",
			"HERO", "HERS", "HESS", "HEWN", "HICK", "HIDE", "HIGH", "HIKE",
			"HILL", "HILT", "HIND", "HINT", "HIRE", "HISS", "HIVE", "HOBO",
			"HOCK", "HOFF", "HOLD", "HOLE", "HOLM", "HOLT", "HOME", "HONE",
			"HONK", "HOOD", "HOOF", "HOOK", "HOOT", "HORN", "HOSE", "HOST",
			"HOUR", "HOVE", "HOWE", "HOWL", "HOYT", "HUCK", "HUED", "HUFF",
			"HUGE", "HUGH", "HUGO", "HULK", "HULL", "HUNK", "HUNT", "HURD",
			"HURL", "HURT", "HUSH", "HYDE", "HYMN", "IBIS", "ICON", "IDEA",
			"IDLE", "IFFY", "INCA", "INCH", "INTO", "IONS", "IOTA", "IOWA",
			"IRIS", "IRMA", "IRON", "ISLE", "ITCH", "ITEM", "IVAN", "JACK",
			"JADE", "JAIL", "JAKE", "JANE", "JAVA", "JEAN", "JEFF", "JERK",
			"JESS", "JEST", "JIBE", "JILL", "JILT", "JIVE", "JOAN", "JOBS",
			"JOCK", "JOEL", "JOEY", "JOHN", "JOIN", "JOKE", "JOLT", "JOVE",
			"JUDD", "JUDE", "JUDO", "JUDY", "JUJU", "JUKE", "JULY", "JUNE",
			"JUNK", "JUNO", "JURY", "JUST", "JUTE", "KAHN", "KALE", "KANE",
			"KANT", "KARL", "KATE", "KEEL", "KEEN", "KENO", "KENT", "KERN",
			"KERR", "KEYS", "KICK", "KILL", "KIND", "KING", "KIRK", "KISS",
			"KITE", "KLAN", "KNEE", "KNEW", "KNIT", "KNOB", "KNOT", "KNOW",
			"KOCH", "KONG", "KUDO", "KURD", "KURT", "KYLE", "LACE", "LACK",
			"LACY", "LADY", "LAID", "LAIN", "LAIR", "LAKE", "LAMB", "LAME",
			"LAND", "LANE", "LANG", "LARD", "LARK", "LASS", "LAST", "LATE",
			"LAUD", "LAVA", "LAWN", "LAWS", "LAYS", "LEAD", "LEAF", "LEAK",
			"LEAN", "LEAR", "LEEK", "LEER", "LEFT", "LEND", "LENS", "LENT",
			"LEON", "LESK", "LESS", "LEST", "LETS", "LIAR", "LICE", "LICK",
			"LIED", "LIEN", "LIES", "LIEU", "LIFE", "LIFT", "LIKE", "LILA",
			"LILT", "LILY", "LIMA", "LIMB", "LIME", "LIND", "LINE", "LINK",
			"LINT", "LION", "LISA", "LIST", "LIVE", "LOAD", "LOAF", "LOAM",
			"LOAN", "LOCK", "LOFT", "LOGE", "LOIS", "LOLA", "LONE", "LONG",
			"LOOK", "LOON", "LOOT", "LORD", "LORE", "LOSE", "LOSS", "LOST",
			"LOUD", "LOVE", "LOWE", "LUCK", "LUCY", "LUGE", "LUKE", "LULU",
			"LUND", "LUNG", "LURA", "LURE", "LURK", "LUSH", "LUST", "LYLE",
			"LYNN", "LYON", "LYRA", "MACE", "MADE", "MAGI", "MAID", "MAIL",
			"MAIN", "MAKE", "MALE", "MALI", "MALL", "MALT", "MANA", "MANN",
			"MANY", "MARC", "MARE", "MARK", "MARS", "MART", "MARY", "MASH",
			"MASK", "MASS", "MAST", "MATE", "MATH", "MAUL", "MAYO", "MEAD",
			"MEAL", "MEAN", "MEAT", "MEEK", "MEET", "MELD", "MELT", "MEMO",
			"MEND", "MENU", "MERT", "MESH", "MESS", "MICE", "MIKE", "MILD",
			"MILE", "MILK", "MILL", "MILT", "MIMI", "MIND", "MINE", "MINI",
			"MINK", "MINT", "MIRE", "MISS", "MIST", "MITE", "MITT", "MOAN",
			"MOAT", "MOCK", "MODE", "MOLD", "MOLE", "MOLL", "MOLT", "MONA",
			"MONK", "MONT", "MOOD", "MOON", "MOOR", "MOOT", "MORE", "MORN",
			"MORT", "MOSS", "MOST", "MOTH", "MOVE", "MUCH", "MUCK", "MUDD",
			"MUFF", "MULE", "MULL", "MURK", "MUSH", "MUST", "MUTE", "MUTT",
			"MYRA", "MYTH", "NAGY", "NAIL", "NAIR", "NAME", "NARY", "NASH",
			"NAVE", "NAVY", "NEAL", "NEAR", "NEAT", "NECK", "NEED", "NEIL",
			"NELL", "NEON", "NERO", "NESS", "NEST", "NEWS", "NEWT", "NIBS",
			"NICE", "NICK", "NILE", "NINA", "NINE", "NOAH", "NODE", "NOEL",
			"NOLL", "NONE", "NOOK", "NOON", "NORM", "NOSE", "NOTE", "NOUN",
			"NOVA", "NUDE", "NULL", "NUMB", "OATH", "OBEY", "OBOE", "ODIN",
			"OHIO", "OILY", "OINT", "OKAY", "OLAF", "OLDY", "OLGA", "OLIN",
			"OMAN", "OMEN", "OMIT", "ONCE", "ONES", "ONLY", "ONTO", "ONUS",
			"ORAL", "ORGY", "OSLO", "OTIS", "OTTO", "OUCH", "OUST", "OUTS",
			"OVAL", "OVEN", "OVER", "OWLY", "OWNS", "QUAD", "QUIT", "QUOD",
			"RACE", "RACK", "RACY", "RAFT", "RAGE", "RAID", "RAIL", "RAIN",
			"RAKE", "RANK", "RANT", "RARE", "RASH", "RATE", "RAVE", "RAYS",
			"READ", "REAL", "REAM", "REAR", "RECK", "REED", "REEF", "REEK",
			"REEL", "REID", "REIN", "RENA", "REND", "RENT", "REST", "RICE",
			"RICH", "RICK", "RIDE", "RIFT", "RILL", "RIME", "RING", "RINK",
			"RISE", "RISK", "RITE", "ROAD", "ROAM", "ROAR", "ROBE", "ROCK",
			"RODE", "ROIL", "ROLL", "ROME", "ROOD", "ROOF", "ROOK", "ROOM",
			"ROOT", "ROSA", "ROSE", "ROSS", "ROSY", "ROTH", "ROUT", "ROVE",
			"ROWE", "ROWS", "RUBE", "RUBY", "RUDE", "RUDY", "RUIN", "RULE",
			"RUNG", "RUNS", "RUNT", "RUSE", "RUSH", "RUSK", "RUSS", "RUST",
			"RUTH", "SACK", "SAFE", "SAGE", "SAID", "SAIL", "SALE", "SALK",
			"SALT", "SAME", "SAND", "SANE", "SANG", "SANK", "SARA", "SAUL",
			"SAVE", "SAYS", "SCAN", "SCAR", "SCAT", "SCOT", "SEAL", "SEAM",
			"SEAR", "SEAT", "SEED", "SEEK", "SEEM", "SEEN", "SEES", "SELF",
			"SELL", "SEND", "SENT", "SETS", "SEWN", "SHAG", "SHAM", "SHAW",
			"SHAY", "SHED", "SHIM", "SHIN", "SHOD", "SHOE", "SHOT", "SHOW",
			"SHUN", "SHUT", "SICK", "SIDE", "SIFT", "SIGH", "SIGN", "SILK",
			"SILL", "SILO", "SILT", "SINE", "SING", "SINK", "SIRE", "SITE",
			"SITS", "SITU", "SKAT", "SKEW", "SKID", "SKIM", "SKIN", "SKIT",
			"SLAB", "SLAM", "SLAT", "SLAY", "SLED", "SLEW", "SLID", "SLIM",
			"SLIT", "SLOB", "SLOG", "SLOT", "SLOW", "SLUG", "SLUM", "SLUR",
			"SMOG", "SMUG", "SNAG", "SNOB", "SNOW", "SNUB", "SNUG", "SOAK",
			"SOAR", "SOCK", "SODA", "SOFA", "SOFT", "SOIL", "SOLD", "SOME",
			"SONG", "SOON", "SOOT", "SORE", "SORT", "SOUL", "SOUR", "SOWN",
			"STAB", "STAG", "STAN", "STAR", "STAY", "STEM", "STEW", "STIR",
			"STOW", "STUB", "STUN", "SUCH", "SUDS", "SUIT", "SULK", "SUMS",
			"SUNG", "SUNK", "SURE", "SURF", "SWAB", "SWAG", "SWAM", "SWAN",
			"SWAT", "SWAY", "SWIM", "SWUM", "TACK", "TACT", "TAIL", "TAKE",
			"TALE", "TALK", "TALL", "TANK", "TASK", "TATE", "TAUT", "TEAL",
			"TEAM", "TEAR", "TECH", "TEEM", "TEEN", "TEET", "TELL", "TEND",
			"TENT", "TERM", "TERN", "TESS", "TEST", "THAN", "THAT", "THEE",
			"THEM", "THEN", "THEY", "THIN", "THIS", "THUD", "THUG", "TICK",
			"TIDE", "TIDY", "TIED", "TIER", "TILE", "TILL", "TILT", "TIME",
			"TINA", "TINE", "TINT", "TINY", "TIRE", "TOAD", "TOGO", "TOIL",
			"TOLD", "TOLL", "TONE", "TONG", "TONY", "TOOK", "TOOL", "TOOT",
			"TORE", "TORN", "TOTE", "TOUR", "TOUT", "TOWN", "TRAG", "TRAM",
			"TRAY", "TREE", "TREK", "TRIG", "TRIM", "TRIO", "TROD", "TROT",
			"TROY", "TRUE", "TUBA", "TUBE", "TUCK", "TUFT", "TUNA", "TUNE",
			"TUNG", "TURF", "TURN", "TUSK", "TWIG", "TWIN", "TWIT", "ULAN",
			"UNIT", "URGE", "USED", "USER", "USES", "UTAH", "VAIL", "VAIN",
			"VALE", "VARY", "VASE", "VAST", "VEAL", "VEDA", "VEIL", "VEIN",
			"VEND", "VENT", "VERB", "VERY", "VETO", "VICE", "VIEW", "VINE",
			"VISE", "VOID", "VOLT", "VOTE", "WACK", "WADE", "WAGE", "WAIL",
			"WAIT", "WAKE", "WALE", "WALK", "WALL", "WALT", "WAND", "WANE",
			"WANG", "WANT", "WARD", "WARM", "WARN", "WART", "WASH", "WAST",
			"WATS", "WATT", "WAVE", "WAVY", "WAYS", "WEAK", "WEAL", "WEAN",
			"WEAR", "WEED", "WEEK", "WEIR", "WELD", "WELL", "WELT", "WENT",
			"WERE", "WERT", "WEST", "WHAM", "WHAT", "WHEE", "WHEN", "WHET",
			"WHOA", "WHOM", "WICK", "WIFE", "WILD", "WILL", "WIND", "WINE",
			"WING", "WINK", "WINO", "WIRE", "WISE", "WISH", "WITH", "WOLF",
			"WONT", "WOOD", "WOOL", "WORD", "WORE", "WORK", "WORM", "WORN",
			"WOVE", "WRIT", "WYNN", "YALE", "YANG", "YANK", "YARD", "YARN",
			"YAWL", "YAWN", "YEAH", "YEAR", "YELL", "YOGA", "YOKE" };

} /* End of class otp */

/*
 * Class for implementing md4 hash algorithm (and hopefully md5 eventually).
 * There are constructors for prepping the hash algorithm (doing the padding,
 * mainly) for a String or a byte[], and an mdcalc() method for generating the
 * hash. The results can be accessed as an int array by getregs(), or as a
 * String of hex digits with toString().
 * 
 * Copyright 1996 Harry Mantakos, harry@cs.umd.edu
 */

class md4 extends md {
	md4(String s) {
		super(s);
	}

	md4(byte in[]) {
		super(in);
	}

	static int F(int x, int y, int z) {
		return ((x & y) | (~x & z));
	}

	static int G(int x, int y, int z) {
		return ((x & y) | (x & z) | (y & z));
	}

	static int H(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	void round1(int blk) {
		A = rotintlft((A + F(B, C, D) + d[0 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[1 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[2 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[3 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[4 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[5 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[6 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[7 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[8 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[9 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[10 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[11 + 16 * blk]), 19);

		A = rotintlft((A + F(B, C, D) + d[12 + 16 * blk]), 3);
		D = rotintlft((D + F(A, B, C) + d[13 + 16 * blk]), 7);
		C = rotintlft((C + F(D, A, B) + d[14 + 16 * blk]), 11);
		B = rotintlft((B + F(C, D, A) + d[15 + 16 * blk]), 19);
	}

	void round2(int blk) {
		A = rotintlft((A + G(B, C, D) + d[0 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[4 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[8 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[12 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[1 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[5 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[9 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[13 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[2 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[6 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[10 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[14 + 16 * blk] + 0x5a827999), 13);

		A = rotintlft((A + G(B, C, D) + d[3 + 16 * blk] + 0x5a827999), 3);
		D = rotintlft((D + G(A, B, C) + d[7 + 16 * blk] + 0x5a827999), 5);
		C = rotintlft((C + G(D, A, B) + d[11 + 16 * blk] + 0x5a827999), 9);
		B = rotintlft((B + G(C, D, A) + d[15 + 16 * blk] + 0x5a827999), 13);

	}

	void round3(int blk) {
		A = rotintlft((A + H(B, C, D) + d[0 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[8 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[4 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[12 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[2 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[10 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[6 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[14 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[1 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[9 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[5 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[13 + 16 * blk] + 0x6ed9eba1), 15);

		A = rotintlft((A + H(B, C, D) + d[3 + 16 * blk] + 0x6ed9eba1), 3);
		D = rotintlft((D + H(A, B, C) + d[11 + 16 * blk] + 0x6ed9eba1), 9);
		C = rotintlft((C + H(D, A, B) + d[7 + 16 * blk] + 0x6ed9eba1), 11);
		B = rotintlft((B + H(C, D, A) + d[15 + 16 * blk] + 0x6ed9eba1), 15);

	}

	void round4(int blk) {
		System.out.println(" must be md5, in round4!");
	}
}

class md5 extends md {
	md5(String s) {
		super(s);
	}

	md5(byte in[]) {
		super(in);
	}

	static int F(int x, int y, int z) {
		return ((x & y) | (~x & z));
	}

	static int G(int x, int y, int z) {
		return ((x & z) | (y & ~z));
	}

	static int H(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	static int I(int x, int y, int z) {
		return (y ^ (x | ~z));
	}

	void round1(int blk) {
		A = rotintlft(A + F(B, C, D) + d[0 + 16 * blk] + 0xd76aa478, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[1 + 16 * blk] + 0xe8c7b756, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[2 + 16 * blk] + 0x242070db, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[3 + 16 * blk] + 0xc1bdceee, 22) + C;

		A = rotintlft(A + F(B, C, D) + d[4 + 16 * blk] + 0xf57c0faf, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[5 + 16 * blk] + 0x4787c62a, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[6 + 16 * blk] + 0xa8304613, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[7 + 16 * blk] + 0xfd469501, 22) + C;
		A = rotintlft(A + F(B, C, D) + d[8 + 16 * blk] + 0x698098d8, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[9 + 16 * blk] + 0x8b44f7af, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[10 + 16 * blk] + 0xffff5bb1, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[11 + 16 * blk] + 0x895cd7be, 22) + C;
		A = rotintlft(A + F(B, C, D) + d[12 + 16 * blk] + 0x6b901122, 7) + B;
		D = rotintlft(D + F(A, B, C) + d[13 + 16 * blk] + 0xfd987193, 12) + A;
		C = rotintlft(C + F(D, A, B) + d[14 + 16 * blk] + 0xa679438e, 17) + D;
		B = rotintlft(B + F(C, D, A) + d[15 + 16 * blk] + 0x49b40821, 22) + C;
	}

	void round2(int blk) {
		A = rotintlft(A + G(B, C, D) + d[1 + 16 * blk] + 0xf61e2562, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[6 + 16 * blk] + 0xc040b340, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[11 + 16 * blk] + 0x265e5a51, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[0 + 16 * blk] + 0xe9b6c7aa, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[5 + 16 * blk] + 0xd62f105d, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[10 + 16 * blk] + 0x02441453, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[15 + 16 * blk] + 0xd8a1e681, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[4 + 16 * blk] + 0xe7d3fbc8, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[9 + 16 * blk] + 0x21e1cde6, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[14 + 16 * blk] + 0xc33707d6, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[3 + 16 * blk] + 0xf4d50d87, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[8 + 16 * blk] + 0x455a14ed, 20) + C;
		A = rotintlft(A + G(B, C, D) + d[13 + 16 * blk] + 0xa9e3e905, 5) + B;
		D = rotintlft(D + G(A, B, C) + d[2 + 16 * blk] + 0xfcefa3f8, 9) + A;
		C = rotintlft(C + G(D, A, B) + d[7 + 16 * blk] + 0x676f02d9, 14) + D;
		B = rotintlft(B + G(C, D, A) + d[12 + 16 * blk] + 0x8d2a4c8a, 20) + C;
	}

	void round3(int blk) {
		A = rotintlft(A + H(B, C, D) + d[5 + 16 * blk] + 0xfffa3942, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[8 + 16 * blk] + 0x8771f681, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[11 + 16 * blk] + 0x6d9d6122, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[14 + 16 * blk] + 0xfde5380c, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[1 + 16 * blk] + 0xa4beea44, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[4 + 16 * blk] + 0x4bdecfa9, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[7 + 16 * blk] + 0xf6bb4b60, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[10 + 16 * blk] + 0xbebfbc70, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[13 + 16 * blk] + 0x289b7ec6, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[0 + 16 * blk] + 0xeaa127fa, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[3 + 16 * blk] + 0xd4ef3085, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[6 + 16 * blk] + 0x04881d05, 23) + C;
		A = rotintlft(A + H(B, C, D) + d[9 + 16 * blk] + 0xd9d4d039, 4) + B;
		D = rotintlft(D + H(A, B, C) + d[12 + 16 * blk] + 0xe6db99e5, 11) + A;
		C = rotintlft(C + H(D, A, B) + d[15 + 16 * blk] + 0x1fa27cf8, 16) + D;
		B = rotintlft(B + H(C, D, A) + d[2 + 16 * blk] + 0xc4ac5665, 23) + C;
	}

	void round4(int blk) {
		A = rotintlft(A + I(B, C, D) + d[0 + 16 * blk] + 0xf4292244, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[7 + 16 * blk] + 0x432aff97, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[14 + 16 * blk] + 0xab9423a7, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[5 + 16 * blk] + 0xfc93a039, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[12 + 16 * blk] + 0x655b59c3, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[3 + 16 * blk] + 0x8f0ccc92, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[10 + 16 * blk] + 0xffeff47d, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[1 + 16 * blk] + 0x85845dd1, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[8 + 16 * blk] + 0x6fa87e4f, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[15 + 16 * blk] + 0xfe2ce6e0, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[6 + 16 * blk] + 0xa3014314, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[13 + 16 * blk] + 0x4e0811a1, 21) + C;
		A = rotintlft(A + I(B, C, D) + d[4 + 16 * blk] + 0xf7537e82, 6) + B;
		D = rotintlft(D + I(A, B, C) + d[11 + 16 * blk] + 0xbd3af235, 10) + A;
		C = rotintlft(C + I(D, A, B) + d[2 + 16 * blk] + 0x2ad7d2bb, 15) + D;
		B = rotintlft(B + I(C, D, A) + d[9 + 16 * blk] + 0xeb86d391, 21) + C;
	}
}

