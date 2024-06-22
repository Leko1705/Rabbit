package computil.generation;

public abstract class BasicIRComponent implements IRComponent {

    final IRMediator mediator;

    protected BasicIRComponent(IRMediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public Function getFunction(String name) {
        return mediator.getFunction(name);
    }

    @Override
    public Struct getStruct(String name) {
        return mediator.getStruct(name);
    }

    protected IRMediator getMediator(){
        return mediator;
    }

}
