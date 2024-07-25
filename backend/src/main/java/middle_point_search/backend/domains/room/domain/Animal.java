package middle_point_search.backend.domains.room.domain;

import java.util.Random;

public enum Animal {
	CAT("고양이"),
	DOG("강아지"),
	TURTLE("거북이"),
	RABBIT("토끼"),
	SNAKE("뱀"),
	LION("사자"),
	TIGER("호랑이"),
	LEOPARD("표범"),
	CHEETAH("치타"),
	HYENA("하이에나"),
	GIRAFFE("기린"),
	ELEPHANT("코끼리"),
	RHINOCEROS("코뿔소"),
	HIPPOPOTAMUS("하마"),
	CROCODILE("악어"),
	PENGUIN("펭귄"),
	OWL("부엉이"),
	OWLET("올빼미"),
	BEAR("곰"),
	PIG("돼지"),
	COW("소"),
	CHICKEN("닭"),
	EAGLE("독수리"),
	OSTRICH("타조"),
	GORILLA("고릴라"),
	ORANGUTAN("오랑우탄"),
	CHIMPANZEE("침팬지"),
	MONKEY("원숭이"),
	KOALA("코알라"),
	KANGAROO("캥거루"),
	WHALE("고래"),
	SHARK("상어"),
	TURKEY("칠면조"),
	BULBUL("직박구리"),
	MOUSE("쥐"),
	SQUIRREL("청설모"),
	QUAIL("메추라기"),
	PARROT("앵무새"),
	LYNX("삵"),
	LYNX_WILDCAT("스라소니"),
	PANDA("판다"),
	BADGER("오소리"),
	DUCK("오리"),
	GOOSE("거위"),
	SWAN("백조"),
	CRANE("두루미"),
	HEDGEHOG("고슴도치"),
	MOLE("두더지"),
	AXOLOTL("아홀로틀"),
	TREE_FROG("맹꽁이"),
	RACCOON("너구리"),
	FROG("개구리"),
	TOAD("두꺼비"),
	CHAMELEON("카멜레온"),
	IGUANA("이구아나"),
	ROE_DEER("노루"),
	SWALLOW("제비"),
	MAGPIE("까치"),
	WATER_DEER("고라니"),
	OTTER("수달"),
	DONKEY("당나귀"),
	REINDEER("순록"),
	GOAT("염소"),
	PEACOCK("공작"),
	SEAL("바다표범"),
	BISON("들소"),
	BAT("박쥐"),
	SPARROW("참새"),
	SEA_LION("물개"),
	SEA_LION_ANIMAL("바다사자"),
	VIPER("살모사"),
	PYTHON("구렁이"),
	ZEBRA("얼룩말"),
	MOUNTAIN_GOAT("산양"),
	WILD_BOAR("멧돼지"),
	CAPYBARA("카피바라"),
	SALAMANDER("도롱뇽"),
	POLAR_BEAR("북극곰"),
	PUMA("퓨마"),
	MEERKAT("미어캣"),
	COYOTE("코요테"),
	LLAMA("라마"),
	WOODPECKER("딱따구리"),
	GOOSE_WILD("기러기"),
	PIGEON("비둘기"),
	SKUNK("스컹크"),
	DOLPHIN("돌고래"),
	CROW("까마귀"),
	HAWK("매"),
	CAMEL("낙타"),
	FOX("여우"),
	DEER("사슴"),
	WOLF("늑대"),
	JAGUAR("재규어"),
	ALPACA("알파카"),
	SHEEP("양"),
	SQUIRREL_ANIMAL("다람쥐"),
	MARTEN("담비");

	private final String koreanName;

	Animal(String koreanName) {
		this.koreanName = koreanName;
	}

	public String getKoreanName() {
		return koreanName;
	}

	private static final Random RANDOM = new Random();

	public static Animal getRandomAnimal() {
		Animal[] animals = values();
		return animals[RANDOM.nextInt(animals.length)];
	}
}
