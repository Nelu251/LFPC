public class Pair {
    int index;
    char value;

    Pair(int i, char v){
        this.index = i;
        this.value = v;
    }
    Pair(char v)
    {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "index=" + index +
                ", value=" + value +
                '}';
    }
}
