package datta.core.paper.utilities.etc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class threeParameters<K, V, T> {
    private K p1;
    private V p2;
    private T p3;

    public void put(K p1, V p2, T p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }
}
