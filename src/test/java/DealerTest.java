import org.junit.Before;
import org.junit.Test;

import com.hangzhou.nn.role.Dealer;
import com.hangzhou.nn.room.Room;

public class DealerTest {
	@Test
	public void testDealer(){
		//Room room=new Room();
		Dealer d=new Dealer(null);
		d.start();
	}
}
