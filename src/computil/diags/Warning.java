package computil.diags;

import computil.util.Location;

public class Warning implements Diagnosis {

    private final String msg;
    private final Location location;

    public Warning(String msg, Location location) {
        this.msg = msg;
        this.location = location;
    }

    @Override
    public Kind getKind() {
        return Kind.WARNING;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
