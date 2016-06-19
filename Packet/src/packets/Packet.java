package packets;

public class Packet {
	
	public static String CHECK_SERVER_ONLINE = wrap("check");
	public static String CONNECT = wrap("connect");
	public static String USER_CONNECT = wrap("USER_CONNECT");
	public static String USER_DISCONNECT = wrap("USER_DISCONNECT");
	public static String USER_TAKEN = wrap("user_taken");
	public static String MOVE = wrap("move");
	public static String CLIENT = wrap("client");
	public static String DISCONNECT = wrap("disconnect");
	public static String PING = wrap("ping");
	
	public static String FORWARD = wrap("forward");
	public static String BACK = wrap("back");
	public static String RIGHT = wrap("right");
	public static String LEFT = wrap("left");
	public static String ROTATE = wrap("rotate");

	private static String wrap(String s) {
		return "$" + s + "$";
	}
}
