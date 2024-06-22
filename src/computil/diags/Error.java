package computil.diags;

import computil.util.Location;

public class Error implements Diagnosis {

    private final String msg;
    private final Location location;

    public Error(String msg, Location location) {
        this.msg = msg;
        this.location = location;
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String toString() {
        return "line " + location.line() + ": " + getMessage();
    }
}
