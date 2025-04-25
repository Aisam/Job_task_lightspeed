package Deep_clone;

import java.util.Arrays;

public class Deep_clone_test {
	public static void main(String[] args) throws Exception {
		Man obj = new Man("a", 1, Arrays.asList("b", "c"));
		System.out.println(obj.toString());

		Man copy = (Man) CopyUtils.deepCopy(obj);
		System.out.println(copy.toString());
		System.out.println(copy != obj);
		System.out.println(copy.getFavoriteBooks().equals(obj.getFavoriteBooks()));
	}
}
