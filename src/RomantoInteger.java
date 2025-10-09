import java.util.HashMap;
import java.util.Map;

class Solution {
    public int romanToInt(String s) {
        int result = 0;
        Map<Character, Integer> romanians = new HashMap<>();
        romanians.put('I', 1);
        romanians.put('V', 5);
        romanians.put('X', 10);
        romanians.put('L', 50);
        romanians.put('C', 100);
        romanians.put('D', 500);
        romanians.put('M', 1000);
         for (int i = 0; i < s.length(); i++){
            int curr = romanians.get(s.charAt(i));
            if(i +1 < s.length() && curr < romanians.get(s.charAt(i+1)) ){
                result -= curr;
            }
            else{
                result += curr;
            }
         }
         return result;

    }
}
