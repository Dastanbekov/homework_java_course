class reverse_integers {
    public int reverse(int x) {
        String s = String.valueOf(x);
        String result = "";
        boolean is_start = false;
        if (s.charAt(0) == '-') {
            result = result + '-';
            for (int i = s.length() - 1; i >= 1; i--) {
                if (s.charAt(i) == '0' && !is_start) {
                    continue;
                }
                result = result + s.charAt(i);
                is_start = true;
            }
        } else if (s.charAt(0) == '0') {
            return 0;
        } else {
            for (int i = s.length() - 1; i >= 0; i--) {
                if (s.charAt(i) == '0' && !is_start) {
                    continue;
                }
                is_start = true;
                result = result + s.charAt(i);
            }
        }
        try {
            int asdf = Integer.parseInt(result);
            return asdf;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
