public class StringStructure {

    public static int[] prefixBorder(String s) {
        int n = s.length();
        int[] bp = new int[n];
        int ptr;
        char currChar;
        for (int i = 1; i < n; i++) {
            ptr = bp[i - 1];
            currChar = s.charAt(i);
            while (ptr > 0 && (currChar != s.charAt(ptr))) {
                ptr = bp[ptr - 1];
            }
            if (currChar == s.charAt(ptr)) {
                bp[i] = ptr + 1;
            }
        }
        return bp;
    }

    public static int[] prefixBorderM(int[] bp) {
        int n = bp.length;
        int[] bpm = new int[n];
        bpm[n - 1] = bp[n - 1];
        for (int i = 1; i < n - 1; i++) {
            if (bp[i] > 0 && bp[i] + 1 == bp[i + 1]) {
                bpm[i] = bpm[bp[i] - 1];
            } else {
                bpm[i] = bp[i];
            }
        }
        return bpm;
    }

    public static int[] prefixBorder(int[] bpm) {
        int n = bpm.length;
        int[] bp = new int[n];
        bp[n - 1] = bpm[n - 1];
        for (int i = n - 2; i > 0; i--) {
            bp[i] = Math.max(bp[i + 1] - 1, bpm[i]);
        }
        return bp;
    }

    public static int[] prefixZBlocks(String s) {
        int n = s.length();
        int l = 0, r = 0;
        int[] zp = new int[n];
        int j, rmi;
        for (int i = 1; i < n; i++) {
            if (i >= r) {
                zp[i] = cmp(s, 0, i);
                l = i;
                r = l + zp[i];
            } else {
                j = i - l;
                rmi = r - i;
                if (zp[j] < rmi) {
                    zp[i] = zp[j];
                } else {
                    zp[i] = rmi + cmp(s, rmi, r);
                }
            }
        }
        return zp;
    }

    private static int cmp(String s, int start1, int start2) {
        int n = s.length();
        int len = 0;
        while (start1 < n
                && start2 < n
                && s.charAt(start1++) == s.charAt(start2++)
        ) {
            len++;
        }
        return len;
    }
}
