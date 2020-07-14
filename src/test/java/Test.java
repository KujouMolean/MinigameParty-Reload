import java.util.*;

public class Test {
    static Random r = new Random();

    public static void main(String[] args) {
        List<Integer> structure = new ArrayList<>();
        int[] height = new int[54];
        Arrays.fill(height, -1);
        for (int i = r.nextInt(4); i < 54; i += r.nextInt(4) + 1) {
            System.out.println(i);
            structure.add(i);
            height[i] = 0;
        }
        Collections.shuffle(structure);
        for (int i = 0; i < 12; i++) {
            for (int j = structure.get(i); j < 54; j++) {
                if (height[j] >= 0)
                    height[j]++;
            }
        }
        System.out.println(structure);
        System.out.println(Arrays.toString(height));
    }
}
