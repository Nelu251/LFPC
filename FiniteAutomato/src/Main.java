import java.util.*;

class Main {
    public static boolean containsOnValue(ArrayList<Pair> list, char v) {
        for (var i : list) {
            if (i.value == v)
                return true;
        }
        return false;
    }

    public static int getIndex(ArrayList<Pair> list, char v) {
        for (var i : list) {
            if (i.value == v)
                return i.index;
        }
        return 0;
    }

    public static void main(String[] args) {
        // matrix indices    0  1    1  2    1  1    2  1    2  0    2  3
        String[] grammar = {"S-aB", "B-aC", "B-bB", "C-bB", "C-aS", "C-c$"};
        int j = 0;

        //adding all the non-terminal elements (non repeatable) into a list with a unique index in order to construct the adj_matrix
        ArrayList<Pair> list = new ArrayList<>();
        for (int i = 0; i < grammar.length; i++) {

            char temp = grammar[i].charAt(0);
            if (!(containsOnValue(list, temp))) {
                list.add(new Pair(j, temp));
                j++;
            }
        }
        //Printing the list with non-terminal values  (just to see)
        for (var i : list) {
            System.out.println(i);
        }
        System.out.println();

        // Constructing the Adjacency matrix for the grammar
        int m, n;
        n = list.size();
        m = list.size() + 1;

        List<Character>[][] adj_matrix = new ArrayList[n][m];
        for (int i = 0; i < n; i++) {
            for (int z = 0; z <= n; z++) {
                adj_matrix[i][z] = new ArrayList<>();
            }
        }

        for (int i = 0; i < grammar.length; i++) {
            if (grammar[i].charAt(grammar[i].length() - 1) == '$') {
                char temp = grammar[i].charAt(grammar[i].length() - 2);
                adj_matrix[getIndex(list, grammar[i].charAt(0))][m - 1].add(temp);
            } else {
                char temp = (grammar[i].charAt(grammar[i].length() - 2));
                adj_matrix[getIndex(list, grammar[i].charAt(0))][getIndex(list, grammar[i].charAt(grammar[i].length() - 1))].add(temp);
            }
        }

        //Printing Adjacency matrix
        System.out.print("Adjacency matrix:");
        for (int i = 0; i < n; i++) {
            System.out.println("\t");
            for (int z = 0; z <= n; z++) {
                if (adj_matrix[i][z].contains('\u0000')) adj_matrix[i][z].add('-');
                System.out.print(adj_matrix[i][z]);
            }
        }

        //Algorithm to find out whether whe given string is compatible with our Finite Automato
        String word = "aabac"; // <--- you can change this string in order to check if it matches or not
        System.out.println("\n");
        System.out.print("String:  " + word);
        int count = 0;
        int x = 0;
        boolean flag = true;
        try {
            while (count < word.length() && (flag == true)) {
                for (int i = 0; i < list.size() + 1; i++) {
                    if (adj_matrix[x][i].contains(word.charAt(count))) {
                        x = i;
                        if (count == word.length() - 1 && i != list.size())
                            flag = false;
                        else flag = true;
                        count++;
                        break;
                    } else {
                        flag = false;
                    }

                }

            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("\nString can't end in terminal element which is not final state");
            flag = false;
        }

        if (flag == false)
            System.out.println("\nIncorrect string");

        if (flag == true) {
            System.out.println("\nCorrect string");
        }

    }
}

