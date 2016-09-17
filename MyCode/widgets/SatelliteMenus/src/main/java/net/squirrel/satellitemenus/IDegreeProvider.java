package net.squirrel.satellitemenus;

/**
 * Interface for providing degrees between satellites.
 *
 * @author Siyamed SINIR
 */
public interface IDegreeProvider {
    int[] getDegrees(int count, int fromDegree, int toDegree);
}
