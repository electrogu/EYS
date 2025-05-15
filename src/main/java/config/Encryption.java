package config;

public class Encryption {
    public String encrypt(String text){
        int max = 9, min = 1;
        int number = (int)Math.floor(Math.random()*(max-min+1)+min);
        
        char chars[] = text.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(char c : chars) {
            c -= number;
            number++;
            sb.append(c);
        }
        return sb.toString()+String.valueOf(number-chars.length);
    }
    public String decrypt(String text){
        int number = Integer.valueOf(text.substring(text.length() - 1));
        StringBuilder sb = new StringBuilder(text);
        sb.deleteCharAt(text.length() - 1);
        String resultString = sb.toString();
        char[] chars = resultString.toCharArray();
        StringBuilder decryptedChars = new StringBuilder();
        for(char c : chars) {
            c += number;
            number++;
            decryptedChars.append(c);
        }
        return decryptedChars.toString();
    }
}
