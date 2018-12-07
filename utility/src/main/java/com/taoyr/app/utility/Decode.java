package com.taoyr.app.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Decode {
    public static String RC4Decode(String paramString1, String paramString2) {
         if ((paramString1 == null) || (paramString2 == null)) {
            return "";
        }
        try {
            paramString1 = new String(RC4Base(HexString2Bytes(paramString1), paramString2), "GBK");
            return paramString1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static byte[] RC4Base(byte[] input, String mKkey) {
        int x = 0;
        int y = 0;
        byte key[] = initKey(mKkey);
        int xorIndex;
        byte[] result = new byte[input.length];

        for (int i = 0; i < input.length; i++) {
            x = (x + 1) & 0xff;
            y = ((key[x] & 0xff) + y) & 0xff;
            byte tmp = key[x];
            key[x] = key[y];
            key[y] = tmp;
            xorIndex = ((key[x] & 0xff) + (key[y] & 0xff)) & 0xff;
            result[i] = (byte) (input[i] ^ key[xorIndex]);
        }
        return result;
    }

    private static byte[] HexString2Bytes(String paramString) {
        int j = paramString.length();
        byte[] arrayOfByte1 = new byte[j / 2];
        try {
            byte[] arrayOfByte2 = paramString.getBytes("GBK");
            int i = 0;
            for (; ; ) {
                if (i >= j / 2) {
                    break;
                }
                arrayOfByte1[i] = uniteBytes(arrayOfByte2[(i * 2)], arrayOfByte2[(i * 2 + 1)]);
                i += 1;
            }
            return arrayOfByte1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayOfByte1;
    }

    private static byte uniteBytes(byte paramByte1, byte paramByte2) {
        return (byte) ((char) ((char) Byte.decode("0x" + new String(new byte[]{paramByte1})).byteValue() << '\004') ^ (char) Byte.decode("0x" + new String(new byte[]{paramByte2})).byteValue());
    }

    private static byte[] initKey(String paramString) {

        byte[] b_key = new byte[0];
        try {
            b_key = paramString.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte state[] = new byte[256];

        for (int i = 0; i < 256; i++) {
            state[i] = (byte) i;
        }
        int index1 = 0;
        int index2 = 0;
        if (b_key == null || b_key.length == 0) {
            return null;
        }
        for (int i = 0; i < 256; i++) {
            index2 = ((b_key[index1] & 0xff) + (state[i] & 0xff) + index2) & 0xff;
            byte tmp = state[i];
            state[i] = state[index2];
            state[index2] = tmp;
            index1 = (index1 + 1) % b_key.length;
        }
        return state;
    }

    public static void main(String[] args) {
        String src = "73476AE9646838FEAFD5919CA1C47BCB6A0DDEE1A2E95887E1FA6DD9C16E3EB1A7A128FE9B4DECBB5CD5D3FF53FAA083D2461FDF5661C0967770671872B0B733CDD9B086B84D6D293BE6018CA3AD8D9AA2D04E737CC76B6D61CE1EEC0E6C85B7E00348AF5EDD47EF7D2E30C8148A9961C4C89C929F9F929D320B435F45A04D8993F4C7D4EC9642941FECE431B64FCBAF47CE181A0B6F6376646D6D7BF2C5C39C9933ECE1473A6A3A914B3890182F5EE21AB32C07DC9D42B173586B1CAE2759BF87423E7C59FD38E8A46B5778AECB08D0D423AB642CAE205340C3A840E24AABCAE92C2C99099B551AE5FA0CF884347AADB78A17558E42C37DF8AAEEA65B983A5FE4301BDCB8F5AF877CA48DA11F29EB1DA96A2222BF9CECD778C9A0D4FC9D00A82F1000D72A651D9CBB885818BEDD4972BC5401D35862CA340BFE450D9D4E9B68B70185B9689DC947E775BA9B6BDB89107A40A75C8363CC4E2B592E0E4F612DE57350985327F920AF7DFF72930F1004C56237F2D6F1DE373F3512BDA270602EC791156E5D8ED11573E9EB68E2F152F0167B1792C48B3191EF1B4A862A031136F11014FE8D8F57AA98A44EDC16F78FD0B40840EADD845BC74EE4E695BC8748B7F3F30FEC8B02CA7B3E7A4D6BA089B43B81C3E1E57DD27AAC244F5DD3DFF28C5D50F0F086A96B33FAA35E0F90A8D82DC295F7EA43A3833A93718F5960C8AB989EDB2FA387A2555D6CCDAA10B22503CA9E3B5AC9011211A0FA666068B65DFFF25D29A1D567EFB533EC727A94492B4A5D1DE5AFFD3023CA17D102D3BA38DF7FE777A9CCEF9D03BF6BE81B99AD9D1502BE35DD677885E0648F929C9282D2320F5ED272D22BFA62F1A1A28FF5C47489FDBFCFD5D96454D66507CB604D3DF8E18DD6984E555D7D8E09389726EA66AB7F96CE8A813E4D94BEE8564229F7F0F9719E43A32D75DA4D7CD3130D66A5B85BB3A0C31B1F6D208FB587FBF180A03B9A74F2C2F77BB36DB3A89B2832E9892BDC970FDB8F584168A2284B6A7521F5D70218DF39C85911A5A306F63D38B271098BBB61E371830C1B5D06F16435A7D310270AD894AAE41F85A446BF8FAFA9417317AB287D586A4A78BAE6D8D79E9881A77F2481E56B96608825E3B7DAECC81C6B66463EE61B6BA5A0455B348045C588BAA185D1CF99E45C230A91A8F0EB7BB7F8CCDE4E6893727511D97F4D436E7962AF04A74320FCA9C486E4FF1D47787CFAA1CD5A5D191D5DE03D0D8201D7BDE182ECDCCF82B3D226C0FDE3B052ACD6B3489908E75B6AE2EC426D2433DD66BB68308A219C18B9F3AF2A69BBD700F0952D73DFB0B8BAFBE2C10B4950C84483AB7DCE2296D8C785B096B0E124BA3F129FA54EE8E1C486BF63A41F91EA32CD2D3E3ECAFF236341A68D17C55CD6D625323131D30E7614F07D514AA328F79438372F5F673C28E60206F30210736402EAAB7A89B91E597290D6FC02942546E3508A4BE8FDA4A53548205A9B672DDCC4681FEC3BBD2A4198036D7DE4E5369259765554738AAF472506EDB81DD25576B6CBA4196CDA209A21002405CD01C8529F796C0974D965B91C88737C2BB073E5AF6D290DBBAA4013E5739ADA6DC6DC484A1687D1A31D8BAC2457789621744516BBD53E9572426CBE8CCB69991211B969AF9479133435192B6115CB7A3F0E3CE4828F48425D492D442D81A386A380C30AD698EE2DC33C3970641EFB026308B59CE959ACCA29D45C1892046BAB19B3129B9307B02E5044031639C745EAE2114EF4EE3B0E3BFA5763431718AE40D040A293221C3787705A3461CF6FB078D22466EAADCBD4B57ED76A5F73A537D54D4D738A543D0C25E97A46C50FA0AE1888C0558191C24FFE773080EF34F1C6A5C0683F183F73D5C6E747CFB7EC0739E5A021C5309E69C1A2124536EE2FF94B01CBB7339C32C784C1E8E57BD57A9570D59E0C058247C8B4B454E7E280F980901896A4087C653365EAC15E2C5BCA06CD7CB68FCEBB1F7ED20C19AABCB176EB5FDE0B5B07E844BD7CABE90B019188E7F7372F3D71D2364EB3011236CA963AEC46D67ECCF00093CF7427FE216B55AC1598B6BDF41D619455E09A9826737FE631D81248AC8F2E8456B2717C48F959B017DD10CBE843663AF99B40035FA07589F520926905ED241D007B95763E01523ED12893F2A986D05B1410A92D5BF7FEF1C5FF1026ADEF0C8BC9FBD38116C6079C019C52D6D971090F657B010D3B448E1243A4715F667CC6480DED7F9D17BB86652018983365C48C23EBEF6ADD13C2891A3B1725F7E1DD2EB67C945843D5F25B6601CFDF306D40FF5615E022F93D5E6811E32FA64AD3AF308A0326C4C1AF0EA6FEB85A95A37A69A9F0CF0B3B2CB0A784F65D089E605B46197C8884C7D9D1E6A917BFE1EE9C781CAF0F46A66692A5B1F961AC911A2EB3F971839EB9A7ED797F1AA5C6DD99FD527A624BE8A2E4BAF48BB3C4AAD03DCF260B2F3FEC8C6D8AC87748D97F4E84C333ED6F0A8D5ADD8317F37862387511DB7AF7F31ECE2EB97396B954BE5B07B59ABA4C37B2B68C6FF07C2FF88A3F08C56AAE8341C0DEA07578FCEA757AE5AB38BB86C29DF9CB25D75366FF9CF04698238FC688DE92F100424C3EF6BAEA312635D93BFE8318656A1A7A868BAF44927483C896731CE26C4F0BF3111F0680ECB53452B81420C944CC6DF630BF60F91F87DE53092A4E7227985472BBC0716B20BE97C49F79115A467CDE7F301CD8FE7CDF91F8F9D7A417CBF5DCA03928933911FF039810C8767225B19693B3920703F45214FD0651826234A0E8D909E8839CF51D4989C7089434887F1B3C787FC269829DB1AC82E8077FC88F36AC798EB8FA97DCE66C37496CEB567122570F8DDBDBF7920E0E6CFEF69A3DAAB24B8FBC7B97F6BA3C740AAE71E2016A506F053E1D70226829AED32A3AF70AA4983005B1252C95353A3B5E77BD6FCE7A3091234B8C3934EC7868D6DA726AB333F2F5429C23B654172E681C0E135E0451C2890A2778C4CA1F093D57414E285A864606FADB10664F1CAB1083CC5A655F23AD9AFB33566411635C81FA53E440C770EA79B42FA45D4E3CA8FF9D695280491D012DAABF6F7C6764087B16EA4B756ACA68D3CD0DEA7BDC2A78774CED0633B0D04AB16D898FC3D0C8AB6C80D9F2D49E42E0043AAE986106EB1907DD913704C32B805355DD36A61062EA97865C096E6C2B87A70FF4539E630D9E42030ED5C0A42D59B0BFF2834639F0C3911E01CC268A813A64B1AD9391FC541CE1BC863E1198443EEDD52B58FCC962A8AD0B8213CE75B054908E7C697CF9C67F917DC24AA19E491840F0EE18C879AC38435E13122BFF130337422BD6C6C498159181C3BEBC1E416021A6B8564CBD684930B3DBD3C8A9E95C2A3A81A8A20E05A3E6160A08E9524229B7BECB5C25745CE4D66B608BC5EF753383B889ECA31AE5189289D04DD0D21A4B9B3222E4595E2989AC2510026FB08A54CE4BE8AAE6B549013694FE57754D57D468C80EE0084170318697F57122FF47C55BADFE1B34419F60F4CBEDC10EE285ADC5ADD2C0EF4A5ABF6733F4341F60955ACE5FEA2B80A810EBEBAEA41B0F3BC8FE66EC68131F652BC0CDFC8B04E3E7FA035FF7A54AC783FD16FDE0FAEB3FC135AC345B7C6B4C6B76DE54F423CE901B10252184A4C59BF81BB7CEE85959B51022DD31D9842833648A12DCEEC49F55ADE8E5B5864C239F5DA7DD3C525C209D1FFA87A445DA02A7C15CD577527E906AE040F13DE10C0C097F04C07DB6AB0339C08BC84B9D42331879AFFBBB3AD20FA5DBE1A387109738B658C88E82B1C7C1279ABFD0E2955EF59A24FA924DB9FD0F3BC5B48A2C6162D8C055225B2DE56FA07CF20175A2C0E645FA60CC2797E8DFB0AADA4E4EF8A81CCB56349D069259BEFE81545BCEE0E4DC456C57FA9D41E0F8335C90BE6D533D91AAF6CFBE6D99B20EB8968C3EEE20629AE278EA4C5583D21E4DB48184BC4B857EA64227FAE97C17C9461F8A2E1061A0F9194525CB046045BCBAAC2935666347D806A8A9A4C9DED041D974124DF7E06256988DEBDE2782D94416C046EAD775EC362E3F0215A8BB9308A745530704784392454D20D7C0885107E71C16A61A5C66764ED0449884CBE9E4D17364355C07D6B2243CFAC0D4418C088D0B2EB6554A4FA68BA6EBDE60155F08D9B3555D4647E2A9F3A149E796D19CA6831B75E3AD5A187E873B104805F01B948A14C25C79A42E43259DC32910B674A1D32BD6CFBF8CD94BB0E3E75EFEE8FC6619CC90B9C7188B2A443BA70F9A9EC37C8EAEA064E78A13BED7D3578562368F5B721822799F946E7255BA003589732F776507EC92CE64F6B1A70103592485CDB6FE46AEF74305762020B90223802E35214C27EDA36D078D5ABE7B1DAC159D634B685AEF80FF960FA47DAF8D4338501D3D7B988988343218997CF0F33403F519445ECA276A5187CDF21B747A8BB14F2846E3E6F109CB049BE4F513911D51C0730CD52389104A8AB1BC962D2133C37F32F64FAACCBE7918B16513F7C6879DE34F59E13CA968CFD6ED3A7D74545A40EE292C5388F581C1B35D685A5D8021241AB9029B5EDFAB2B319A0D2AF6514462670A5CF5198055D31D7B196482BC2A82AACBF823325FE1F43279AFB741BA94253E5D443F328ACBFFA9AD43CB08EBF0E3EFCBCEE53324A8DEFBA47C257E077627BABAABE588C8BDB90C0627AA4CF6A654FDC1BAE9052F294857B28D1D6BCF30ED142432DA43B4B67D11E8FE8C1823912BB7E37A389374A5E5F7B63A2EC11CCC6B7AE5400B84968A5656500CC73E272200B5EAA6CA5CC7A67A816989574742344CB32A4369284C678AF83C915C2DD824EE353F3DFC205D64788064B24B7DB728A06F14EFED57837C24D17172D0B097B7001136EA73C957926948E7913FF535B33E45DDAB0019B87A1B3BE8FD5BA3B2B49667F1B816F3F61B632D57BF9CD54E22221AF03014160DAA00AA7105E7834A9FB1FC784DA0E569D57B7644DEF0AF9EA7";
        String res = RC4Decode(src, "1481346264");
        System.out.println(TextUtil.getText2(res, "更新时间", "|"));
        String[] data = TextUtil.splitText(TextUtil.getText2(res, "{", "}"), "\n");
        for(String en:data){
            String str1 = TextUtil.getText2(en, "@mc", "|");
            String str2 = TextUtil.getText2(en, "@tp", "|");
            String str3 = TextUtil.getText2(en, "@dz", "|");
            System.out.println(str1);
            System.out.println(str2);
            System.out.println(str3);
        }
    }
}
