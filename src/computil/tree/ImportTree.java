package computil.tree;

public interface ImportTree extends Tree {

    String getAbsolutePath();

    String getImportedName();
}
