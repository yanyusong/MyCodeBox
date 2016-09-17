package net.squirrel.satellitemenus;

/**
 * Linearly distributes satellites in the given total degree.
 *
 * @author Siyamed SINIR
 */
public class LinearDegreeProvider implements IDegreeProvider {
    public int[] getDegrees(int count, int fromDegree, int toDegree) {
        if (toDegree < fromDegree) {
            toDegree += 360;
        }
        int totalDegrees = toDegree - fromDegree;
        if (count < 1) {
            return new int[]{};
        }

        int[] result = null;
        int tmpCount = count - 1;

        result = new int[count];
        int delta = totalDegrees / tmpCount;
        if (totalDegrees >= 360) {
            delta = totalDegrees / count;
        }

        for (int index = 0; index < count; index++) {
            int tmpIndex = index;
            result[index] = tmpIndex * delta + fromDegree;
        }
        return result;
    }
}
