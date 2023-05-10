import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SuffixTree {

    private static final int FIRST_ALPHABET_CHAR = '$';
    private static final int LAST_ALPHABET_CHAR = 'я';
    private static final int ALPHABET_LENGTH = LAST_ALPHABET_CHAR - FIRST_ALPHABET_CHAR + 1;

    static Node root;

    static class Arc {
        int iBeg, iEnd, iDest;
        Node dest, src;

        public Arc(Node src, int iBeg, int iEnd, Node dest, int iDest) {
            this.iBeg = iBeg;
            this.iEnd = iEnd;
            this.iDest = iDest;
            this.dest = dest;
            this.src = src;
        }
    }

    static class Node {
        Arc[] out;
        Node ref;
        Arc in;

        public Node(Arc in) {
            this.in = in;
            this.out = new Arc[ALPHABET_LENGTH];
        }

        public void addArc(char index, Arc arc) {
            this.out[index - FIRST_ALPHABET_CHAR] = arc;
        }

        public Arc getArc(char index) {
            return this.out[index - FIRST_ALPHABET_CHAR];
        }
    }

    static Arc createArc(Node src, char index, int iBeg, int iEnd, Node dest, int iDest) {
        Arc arc = new Arc(src, iBeg, iEnd, dest, iDest);
        src.addArc(index, arc);
        return arc;
    }

    static Arc findArc(String str, String substr, int m, Node node, int[] iSubArc) {
        iSubArc[0] = 0;
        iSubArc[1] = 0;
        Node curr = node;
        Arc arc = null, nextArc;
        boolean stop = false;
        while (!stop && curr != null) {
            nextArc = curr.getArc(substr.charAt(iSubArc[0]));
            if (nextArc != null) {
                arc = nextArc;
                iSubArc[1] = arc.iBeg;
                while (++iSubArc[0] < m && ++iSubArc[1] <= arc.iEnd
                        && substr.charAt(iSubArc[0]) == str.charAt(iSubArc[1])) ;
                if (iSubArc[1] <= arc.iEnd) stop = true;
                else curr = arc.dest;
            } else stop = true;
        }
        if (iSubArc[0] == m) iSubArc[1]++;
        return arc;
    }

    static Arc findArc(String str, String substr, int m, int mSame, Node start, int iLeavesEnd, int[] iSubArc) {
        Arc arc = null, nextArc;
        iSubArc[0] = iSubArc[1] = 0;
        if (m == 0) return null;
        Node curr = start;
        boolean stop = false;
        int iEnd, sameRest, lenArc;
        while (!stop && curr != null) {
            nextArc = curr.getArc(substr.charAt(iSubArc[0]));
            if (nextArc != null) {
                arc = nextArc;
                iSubArc[1] = arc.iBeg;
                iEnd = arc.dest == null ? iLeavesEnd : arc.iEnd;
                sameRest = mSame - iSubArc[0];
                if (sameRest > 0) {
                    lenArc = iEnd - arc.iBeg + 1;
                    if (sameRest <= lenArc) {
                        iSubArc[0] = mSame - 1;
                        iSubArc[1] += sameRest - 1;
                    } else {
                        iSubArc[0] += lenArc;
                        iSubArc[1] = iEnd + 1;
                        curr = arc.dest;
                        continue;
                    }
                }
                while (++iSubArc[0] < m && ++iSubArc[1] <= iEnd
                        && substr.charAt(iSubArc[0]) == str.charAt(iSubArc[1])) ;
                if (iSubArc[1] <= iEnd) stop = true;
                else curr = arc.dest;
            } else stop = true;
        }
        if (iSubArc[0] == m) iSubArc[1]++;
        return arc;
    }

    static Arc topJumpBottom(String str, String substr, int m, Arc arc,
                             int iArcEnd, int iLeavesEnd, int[] iSubArc) {
        if (arc == null) return null;
        boolean isInner = arc.dest != null && iSubArc[1] > iArcEnd;
        Node src = isInner ? arc.dest : arc.src;
//        if (src == null) return null;
        Node ref = src.ref == null ? src : src.ref;
        int nCharsUp = (src.ref == null ? 0 : 1) + (isInner ? 0 : iSubArc[1] - arc.iBeg);
        int iStart = m - nCharsUp;
        Arc nextArc = findArc(str, substr.substring(iStart), nCharsUp, nCharsUp - 1, ref, iLeavesEnd, iSubArc);
        if (nextArc == null) {
            nextArc = ref.in;
            if (nextArc != null) {
                int iEnd = nextArc.dest == null ? iLeavesEnd : nextArc.iEnd;
                iSubArc[1] = iEnd + 1;
            }
        }
        iSubArc[0] += iStart;
        return nextArc;
    }

    static Node buildTree(String str) {
        if (!str.endsWith("$")) str = str + '$';
        int n = str.length();
        root = new Node(null);
        Arc arc = createArc(root, str.charAt(0), 0, 0, null, 0);
        int js = 0, iEndPrev = -1, m;
        Arc prevArc = null, uv, wv;
        Node refFrom = null, w;
        int[] iSubArc = new int[2];
        for (int i = 1; i < n; i++) {
            for (int j = js; j <= i; j++) {
                m = i - j + 1;
                if (j == js) {
                    prevArc = arc;
                    iEndPrev = i - 1;
                    iSubArc[1] = i;
                    iSubArc[0] = m - 1;
                }
                uv = topJumpBottom(str, str.substring(j), m, prevArc, iEndPrev, i, iSubArc);
                if (iSubArc[0] == m) {
                    js = j;
                    break;
                }
                prevArc = uv;
                if (prevArc == null) iEndPrev = -1;
                else if (prevArc.dest == null) iEndPrev = i;
                else iEndPrev = prevArc.iEnd;
                w = uv == null ? root : uv.dest;
                if (uv != null && iSubArc[1] <= iEndPrev) {
                    w = new Node(uv);
                    wv = createArc(w, str.charAt(iSubArc[1]), iSubArc[1],
                            uv.iEnd, uv.dest, uv.iDest);
                    if (uv.dest != null) uv.dest.in = wv;
                    uv.dest = w;
                    uv.iDest = -1;
                    uv.iEnd = iSubArc[1] - 1;
                    if (refFrom != null) refFrom.ref = w;
                    refFrom = w;
                } else {
                    if (refFrom != null) refFrom.ref = w;
                    refFrom = null;
                }
                arc = createArc(w, str.charAt(i), i, i, null, j + 1);
            }
        }
        return root;
    }

    static void traversal(Arc start, Set<Integer> set) {
        if (start == null) {
            for (Arc a : root.out) {
                if (a != null) traversal(a, set);
            }
        } else {
            if (start.iDest >= 0) {
                set.add(start.iDest);
            } else {
                Node node = start.dest;
                for (Arc a : node.out) {
                    if (a != null) traversal(a, set);
                }
            }
        }
    }

    static Set<Integer> search(String text, String pattern) {
        int m = pattern.length();
        Node root = buildTree(text);
        int[] iSubArc = new int[2];
        Arc arc = findArc(text, pattern, m, root, iSubArc);
        Set<Integer> result = new TreeSet<>();
        if (iSubArc[0] >= m)
            traversal(arc, result);
        return result;
    }

    public static void main(String[] args) {
//        Scanner scn = new Scanner(System.in);
//        System.out.println("Введите текст:");
//        String text = scn.nextLine();
//        System.out.println("Введите шаблон:");
//        String pattern = scn.nextLine();
//        Set<Integer> set = search(text, pattern);
//        StringBuilder sb = new StringBuilder();
//        sb.append("Найдено вхождений: ").append(set.size()).append('\n');
//        for (int i : set)
//            sb.append(i).append(" ");
//        System.out.println(sb);
    }
}
