import java.util.ArrayList;
import java.util.List;

public class PatternFinder {

    public static boolean[] algShiftAnd(String pattern, String text) {
        int m = pattern.length();
        int n = text.length();
        char chBeg = '0';
        char chEnd = 'ÿ';
        int alphLength = chEnd - chBeg + 1;
        long[] b = new long[alphLength];
        boolean[] result = new boolean[n];
        for (int j = 0; j < m; j++) {
            b[pattern.charAt(j) - chBeg] |= 1L << (m - 1 - j);
        }
        long uHigh = 1L << (m - 1);
        long v = 0;
        for (int i = 0; i < n; i++) {
            v = (v >> 1 | uHigh) & b[text.charAt(i) - chBeg];
            if ((v & 1L) != 0) {
                result[i - m + 1] = true;
            }
        }
        return result;
    }

    public static boolean[] algBoyerMoore(String pattern, String text) {
        List<List<Integer>> alphabet = initAlphabet('ÿ');
        findContainmentIndexes(alphabet, pattern);
        int m = pattern.length();
        int n = text.length();
        int textBorder = m;
        int k, i;
        boolean[] result = new boolean[n];
        while (textBorder <= n) {
            k = m - 1;
            i = textBorder - 1;
            for (; k >= 0; k--, i--) {
                if (pattern.charAt(k) != text.charAt(i)) {
                    break;
                }
            }
            if (k < 0) {
                result[++i] = true;
            }
            textBorder += badCharShift(alphabet, text.charAt(i), k);
        }
        return result;
    }

    private static int badCharShift(List<List<Integer>> alphabet, char ch, int pos) {
        if (pos < 0) {
            return 1;
        }
        int nPos = -1;
        List<Integer> currList = alphabet.get(ch);
        for (Integer v : currList) {
            if (v < pos) {
                nPos = v;
                break;
            }
        }
        return pos - nPos;
    }

    private static List<List<Integer>> initAlphabet(char last) {
        List<List<Integer>> alphabet = new ArrayList<>(last);
        for (int i = 0; i < last; i++) {
            alphabet.add(new ArrayList<>());
        }
        return alphabet;
    }

    private static void findContainmentIndexes(List<List<Integer>> alphabet, String pattern) {
        for (int i = pattern.length() - 1; i >= 0; i--) {
            alphabet.get(pattern.charAt(i)).add(i);
        }
    }

    public static boolean[] algKarpRabin(String pattern, String text, int module) {
        int m = pattern.length();
        int n = text.length();
        int p2m = 1;
        for (int i = 0; i < m - 1; i++) {
            p2m = (p2m << 1) % module;
        }
        int hp = gorner(pattern, m, module);
        int ht = gorner(text, m, module);
        int k;
        boolean[] res = new boolean[n];
        for (int j = 0; j <= n - m; j++) {
            if (ht == hp) {
                k = 0;
                while (k < m && pattern.charAt(k) == text.charAt(j + k)) {
                    k++;
                }
                if (k == m) {
                    res[j] = true;
                }
            }
            if (j < n - m) {
                ht = ((ht - (p2m * text.charAt(j)) << 1) + text.charAt(j + m)) % module;
                if (ht < 0) {
                    ht += module;
                }
            }
        }
        return res;
    }

    private static int gorner(String s, int m, int module) {
        int res = 0;
        for (int i = 0; i < m; i++) {
            res = ((res << 1) + s.charAt(i)) % module;
        }
        return res;
    }
}
