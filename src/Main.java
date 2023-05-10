import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        boolean[] res = PatternFinder.algBoyerMoore("aba", "abafarqwrqababafaba");
        Scanner scn = new Scanner(System.in);
        System.out.println("������� ������:");
        String text = scn.nextLine();
        System.out.println("������� ������� ������:");
        String pattern = scn.nextLine();
        StringBuilder sb = new StringBuilder();
//        boolean[] res = PatternFinder.algBoyerMoore(pattern, text);
//        boolean[] res = PatternFinder.algShiftAnd(pattern, text);
        boolean[] res = PatternFinder.algKarpRabin(pattern, text, 11);
        sb.append("��������� ������� � �������:\n");
        for (int i = 0; i < res.length; i++) {
            if (res[i]) {
                sb.append(i).append(" ");
            }
        }
        System.out.println(sb);
    }
}
