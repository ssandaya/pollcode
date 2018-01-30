class ExampleListArgs {
    static void main(String[] args) {
        def arg1 = args[0]
        def arg2 = args[1]
        def arg3 = args[2]

        println "${arg1} ${arg2} ${arg3}"


        def lst = [11, 12, 13, 14];

        println lst.head();
        println lst.tail();

        println(lst.remove(0));
        println(lst);
        for (int i in lst) {
            println i
        }

        println(lst.pop());
        println(lst);

    }
}