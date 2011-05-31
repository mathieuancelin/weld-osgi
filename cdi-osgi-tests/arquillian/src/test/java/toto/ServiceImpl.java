package toto;

@Name("alfred")
public class ServiceImpl implements Service {
    @Override
    public String whoAmI() {
        return getClass().getName();
    }
}
