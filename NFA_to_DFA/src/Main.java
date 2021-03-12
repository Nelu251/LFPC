import java.util.*;

public class Main {

    public static int extractMaximum(String str) {
        int num = 0, res = 0;
        for (int i = 0; i < str.length(); i++)

            if (Character.isDigit(str.charAt(i)))
                num = num * 10 + (str.charAt(i) - '0');
            else {
                res = Math.max(res, num);
                num = 0;
            }
        return Math.max(res, num);
    }

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
        String[] grammar = {"(q0,a)=q1", "(q1,b)=q2", "(q2,c)=q3", "(q3,a)=q1", "(q1,b)=q1", "(q0,b)=q2"};
        String[] Final_State = {"q3"};
        Integer [] Finalstate = new Integer[Final_State.length];
        for (int i = 0; i < Final_State.length; i++ ) {
            Finalstate[i] = Final_State[i].charAt(1)-'0';
        }
        int max = 0;
        for (var i : grammar) {
            int currentmax = extractMaximum(i);
            if (currentmax > max)
                max = currentmax;
        }

        int j = 0;
        ArrayList<Pair> Vt = new ArrayList<>();
        for (int i = 0; i < grammar.length; i++) {
            char temp = grammar[i].charAt(4);
            if (!(containsOnValue(Vt, temp))) {
                Vt.add(new Pair(j, temp));
                j++;
            }
        }

        max++;
        //Creating transition table for nfa
        HashSet<Integer>[][] tranz_table = new HashSet[max][Vt.size()];
        for (int i = 0; i < max; i++) {
            for (int z = 0; z < Vt.size(); z++) {
                tranz_table[i][z] = new HashSet<>();
            }
        }

        for (var i : grammar) {
            tranz_table[i.charAt(2) - '0'][getIndex(Vt, i.charAt(4))].add(i.charAt(8) - '0');
        }


        LinkedList<HashSet<Integer>> order = new LinkedList<>();
        for (int i = 0; i < max; i++) {
            for (int z = 0; z < Vt.size(); z++) {
                if (tranz_table[i][z].size() > 1) {
                    order.add(tranz_table[i][z]);
                }
            }
        }
        //Algorithm for NFA -> DFA
        j = 0;
        int index = 0;
        System.out.println();
        HashSet<Integer> temp = new HashSet<>();
        for (j = 0; j < order.size(); j++) {
            for (int i = 0; i < Vt.size(); i++) {
                for (int set_index : order.get(j)) {
                    temp.addAll(tranz_table[set_index][i]);
                }
                if (!(order.contains(temp)) && temp.size() > 1) {
                    order.add(index + 1, new HashSet<>(temp));
                }
                temp.clear();
            }
            index++;
        }

        HashSet<Integer>[][] tranz_table_dfa = new HashSet[max + order.size()][Vt.size()];

        LinkedList<HashSet<Integer>> nodes = new LinkedList<>();
        for (int i = 0; i < max; i++) {
            nodes.add(i, new HashSet<>(Arrays.asList(i)));
        }
        nodes.addAll(order);

        for (int i = 0; i < max + order.size(); i++) {
            for (int z = 0; z < Vt.size(); z++) {
                tranz_table_dfa[i][z] = new HashSet<>();
            }
        }
        index = 0;
        for (j = 0; j < nodes.size(); j++) {
            for (int i = 0; i < Vt.size(); i++) {
                for (int set_index : nodes.get(j)) {
                    tranz_table_dfa[j][i].addAll(tranz_table[set_index][i]);
                }
            }
        }

        //Printing the tables
        System.out.println("Grammar:");
        System.out.println(Arrays.toString(grammar));
        System.out.println("Final states: ");
        System.out.println(Arrays.toString(Final_State));
        System.out.println();

        System.out.println("NFA:");
        System.out.print("     ");
        for (var i: Vt) {
            System.out.print(i.value+"  ");
        }
        System.out.println();
        convert(tranz_table, max, Vt.size(),nodes,Finalstate);

        System.out.println("\n");

        System.out.println("DFA:");
        System.out.print("     ");
        for (var i: Vt) {
            System.out.print(i.value+"  ");
        }
        System.out.println();
        convert(tranz_table_dfa, nodes.size(), Vt.size(), nodes,Finalstate);

    }

    //function to convert table with just numbers to states
    public static void convert(HashSet<Integer>[][] arr, int n, int m, LinkedList<HashSet<Integer>> list,Integer[] finalstate) {

        Set<Integer> targetSet = new HashSet<>(Arrays.asList(finalstate));

        HashSet<String>[][] table = new HashSet[n][m];
        ArrayList<String> list1 = new ArrayList<>(list.size());

        for(int i = 0; i< list.size(); i++){

            StringBuilder temp1 = new StringBuilder();
            for (var j: list.get(i)) {
                String temp = "q"+String.valueOf(j);
                temp1.append(temp);
            }
            list1.add(temp1.toString());
        }
        int index= 0;
        for (var i: list) {
            if (i.stream().anyMatch(targetSet::contains)){
                String temp ="*"+list1.get(index);
                list1.set(index,temp);
            }
            index++;
        }
        list1.set(0, ">"+list1.get(0));


        for (int i = 0; i < n; i++) {
            for (int z = 0; z < m; z++) {
                table[i][z] = new HashSet<>();
            }
        }


        for (int i = 0; i < n; i++) {
            System.out.print(list1.get(i)+":");
            for (int j = 0; j < m; j++) {
                if (!arr[i][j].isEmpty()) {
                    for (int k : arr[i][j]) {
                        table[i][j].add("q" + k);
                    }

                }

                System.out.print(table[i][j]);
            }
            System.out.println();
        }
    }


}
