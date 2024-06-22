package computil.tree;

import computil.util.Location;
import computil.util.TreeVisitor;

public interface Tree {

    Location getLocation();

    <P, R> R accept(TreeVisitor<P, R> visitor, P p);

    default boolean replace(Tree oldTree, Tree newTree){
        return false;
    }

    default boolean remove(Tree toRemove){
        return false;
    }

}
