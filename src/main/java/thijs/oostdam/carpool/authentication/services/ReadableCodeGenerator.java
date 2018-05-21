package thijs.oostdam.carpool.authentication.services;

import com.google.common.base.Charsets;

import java.util.Random;

public class ReadableCodeGenerator {
    private Random random;

    public ReadableCodeGenerator(){
        this.random = new Random();
    }

    public String generateCode(int length){

        //generate verification code
        byte[] code = new byte[length];
        for(int i = 0; i < code.length; i++){
            //https://en.wikipedia.org/wiki/Basic_Latin_(Unicode_block)
            code[i] = (byte)(random.nextInt(26) +65);
        }

        return new String(code, Charsets.UTF_8);
    }
}
