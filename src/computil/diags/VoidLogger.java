package computil.diags;

public class VoidLogger implements Logger {

    @Override
    public void error(Error error) { }

    @Override
    public void warning(Warning warning) { }
}
