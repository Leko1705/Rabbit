package computil.check;

import computil.scope.Entry;
import computil.scope.Scope;

public class DefinitionChecker extends Checker<Scope<DefinitionChecker.DefEntry>, Void> {





    public record DefEntry(String name) implements Entry {
        @Override
        public String name() {
            return name;
        }
    }

}
