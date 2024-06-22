package computil.diags;

import computil.util.Location;

public interface Diagnosis {

    enum Kind {
        ERROR,
        WARNING
    }

    Kind getKind();

    String getMessage();

    Location getLocation();

}
