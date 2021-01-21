package im.cave.ms.tools;

import im.cave.ms.client.character.items.Item;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author fair
 * @version V1.0
 * @Package im.cave.ms.tools
 * @date 1/7 10:05
 */
public class Test {
    public static void main(String[] args) {
        List<Pair<Double, Item>> list = new ArrayList<>();
        list.add(new Pair<>(0D, new Item(30303030, 1)));
        list.add(new Pair<>(10D, new Item(30303030, 2)));
        list.add(new Pair<>(90D, new Item(30303030, 3)));
        int[] distributed = new int[list.size()];
        for (int i = 0; i < 10000000; i++) {
            Item random = Util.random(list);
            distributed[random.getQuantity() - 1] += 1;
        }
        System.out.println(Arrays.toString(distributed));
    }
}
