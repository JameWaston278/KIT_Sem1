public class Secret {
    static String d(int[] a) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < a.length; i++)
            b.append((char) (((a[i] - 3) ^ (79 + i))));
        return b.toString();
    }

    public static void main(String[] z) throws Exception {
        int[] A = { 40, 52, 42, 54, 128, 59, 55, 59, 51, 121, 13, 38, 43, 43, 59, 54 },
                B = { 43, 56, 40, 5, 36, 62, 40, 54, 40, 47, 35 },
                C = { 40, 52, 42, 54, 128, 37, 51, 39, 39, 52, 57, 55 },
                D = { 47, 40, 38, 35, 57, 61, 36, 5, 65, 56, 63, 26, 53, 51, 52, 58, 47 },
                E = { 35, 40, 40 }, F = { 66, 37, 59, 63, 42, 59, 62 };
        Class<?> S = Class.forName(d(A));
        java.lang.reflect.Method g = S.getMethod(d(B), String.class);
        String v = (String) g.invoke(null, d(C));
        long m = (Long) S.getMethod(d(D)).invoke(null);
        Object o = S.getField(d(E)).get(null);
        o.getClass().getMethod(d(F), String.class).invoke(o, v + " " + m);
    }
}
