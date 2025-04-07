package shared;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.Test;

import shared.User.Gender;

class DataValidationTest {
	static Random rand = new Random(65536);

	@Test
	void validatePeselTest() {
		String[] pesele = {
			"34032681478",
			"73020961251",
			"36082265798",
			"43040688425",
			"35062736512",
			"52022891515",
			"10090827441",
			"25032622677",
			"94060545267",
			"20011618742",
			"19043024898",
			"23112958458",
			"68112245652",
			"03020913491",
			"29121587971",
			"18083159582",
			"87071845765",
			"99032354555",
			"10051193611",
			"57062466635",
			"32101426157",
			"86021544839",
			"95043087152",
			"82072891128",
			"14101996968",
			"00022161747",
			"42110331463",
			"51080319843",
			"52052961752",
			"72070362531"
		};
		
		for(String s : pesele) {
			Date date = DataValidation.dateFromPesel(s);
			
			Gender g = Gender.values()[Integer.parseInt(s.substring(9, 10)) % 2];
			
			assertTrue(DataValidation.validatePesel(s, date, g));
		}
	}
	
	@Test
	void validatePeselTestFail() {
		String[] pesele = {
			"34032682478",
			"73020931251",
			"36082465798",
			"43040658425",
			"35062766512",
			"52022791515",
			"10090887441",
			"25032629677",
			"94060541267",
			"20011612742",
			"19043034898",
			"23112948458",
			"68112255652",
			"03020963491",
			"29121787971",
			"18083189582",
			"87071895765",
			"99032154555",
			"10051123611",
			"57062463635",
		};
		
		for(String s : pesele) {
			Date date = DataValidation.dateFromPesel(s);
			
			Gender g = Gender.values()[Integer.parseInt(s.substring(9, 10)) % 2];
			
			try {
				DataValidation.validatePesel(s, date, g);
				
				fail("Powinno wyrzucić wyjątek!");
			} catch(IllegalArgumentException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	@Test
	void randomPeselTest() {
		for(int i = 0; i < 100; ++i) {
			Gender g = Gender.values()[(rand.nextInt(10)) % 2];
			Date d = DataValidation.randomDate();
			
			String pesel = DataValidation.randomPesel(d, g);
			assertTrue(DataValidation.validatePesel(pesel, d, g));
		}
	}
	
	@Test
	void validateEmailTest() {
		String[] emails = {
				"dariusz.nowak@gmail.com",
				"dariusznowak@gmail.com",
				"mariusznowak@gmail.com",
				"mariusznowak@outlook.com",
				"justynakowalska@uni.lodz.pl",
				"justyna-kowalska@edu.uni.lodz.pl",
				"michalek_patyczkowski123456789@edu.uni.lodz.pl",
		};
		
		for(String e : emails) {
			assertTrue(DataValidation.validateEmail(e));
		}
	}
	
	@Test
	void validateEmailTestFail() {
		String[] emails = {
				"dariusz.nowakgmail.com",
				"dariusz@nowak@gmail.com",
				"mari\nusznowak@gmail.com",
				"mariusz nowak@outlook.com",
				"ykadytyrijxzmvuqxfpzujaxjigmcqciqcywphvyjwttmqfqvmmxtqvzycjkiafggdxekyzdrahcahvzapncckymvtggwxzyvpqckutkpfqyruvcvkhcwtxccpwybepinffuxrnjirpjmdgazxkrnqijhuvhzqueiqnpncgkzafguzbtkarbdaxzfhaiqbngkehmfimjmrabqfmwmfkbwmyjajzqrmnqhipvinupbtnyfbiaiuejrggdpxqbmxd@gmail.com"
		};
		
		for(String e : emails) {
			try {
				DataValidation.validateEmail(e);
				fail("Powinno wyrzucić wyjątek");
			} catch(IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	@Test
	void validateNrTelTest() {
		String[] nrTel = {
				"668765256",
				"668763256",
				"664763256",
				"164763256",
				"165763256",
				"165763856",
				"165763896",
		};
		
		for(String nr : nrTel) {
			assertTrue(DataValidation.validateNrTel(nr));
		}
	}
	
	@Test
	void validateNrTelTestFail() {
		String[] nrTel = {
				"+ 48 668 765 256",
				"6a8763256",
				"6647b3256",
				"164-763-256",
				"165763c56",
				"16576385612",
				"1657638963333",
		};
		
		for(String nr : nrTel) {
			try {
				DataValidation.validateNrTel(nr);
				fail("Powinno wyrzucić wyjątek");
			} catch(IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	@Test
	void validateDateTest() {
		String[] daty = {
				"03-04-1999",
				"02-05-2036",
				"31-12-1910",
				"29-02-2016",
		};
		
		for(String d : daty) {
			assertNotNull(DataValidation.validateDate(d));
		}
	}
	
	@Test
	void validateDateTestFail() {
		String[] daty = {
				"2002-01-03",
				"02 05 2036",
				"32-12-1910",
				"29-02-2017",
		};
		
		for(String d : daty) {
			try {
				DataValidation.validateDate(d);
				fail("Powinno wyrzucić wyjątek");
			} catch(IllegalArgumentException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
	
	@Test
	void validateDateFromPeselTest() {
		for(int i = 0; i < 100; ++i) {
			Date d = DataValidation.randomDate();
			Gender g = Gender.values()[(rand.nextInt(10)) % 2];
			
			String pesel = DataValidation.randomPesel(d, g);
			Date d2 = DataValidation.dateFromPesel(pesel);
			
			assertEquals(d, d2);
		}
	}
}
