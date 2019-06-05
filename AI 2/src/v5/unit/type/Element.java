package v5.unit.type;

public abstract class Element {

    public abstract boolean equals(Element element);
    public abstract Element addLast(Element element);
    public abstract Element sub(int begin, int size);
    public abstract int size();
    public abstract Element getCopy();
    public abstract boolean isSpecial();

}
