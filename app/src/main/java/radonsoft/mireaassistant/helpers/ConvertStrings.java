package radonsoft.mireaassistant.helpers;


public class ConvertStrings {
    public String instituteNumber;
    public String instituteOutput;

    public String translitInput;
    public String translitOutput;
    //метод перевода instituteID в строку
    public void convertInstitutes(){
        switch (instituteNumber){
            case "0":
                instituteOutput = "Институт информационных технологий" ;
                break;
            case "1":
                instituteOutput = "Физико-технологический институт";
                break;
            case "3":
                instituteOutput = "Институт кибернетики";
                break;
            case "2":
                instituteOutput = "Институт инновационных технологий и государственного управления";
                break;
            case "4":
                instituteOutput = "Институт комплексной безопасности и специального приборостроения";
                break;
            case "5":
                instituteOutput = "Институт радиотехнических и телекоммуникацонных систем";
                break;
            case "7":
                instituteOutput = "Институт управления и стратегического развития организаций";
                break;
            default:
                instituteOutput = String.valueOf(instituteNumber);
                break;
        }
    }
    //метод обратной транслитерации
    public void translitGroups(){
        translitEngine("e`", "Э");
        translitEngine("yu", "Ю");
        translitEngine("a", "А");
        translitEngine("b", "Б");
        translitEngine("c", "Ц");
        translitEngine("d", "Д");
        translitEngine("e", "Е");
        translitEngine("f", "Ф");
        translitEngine("g", "Г");
        translitEngine("j", "Й");
        translitEngine("k", "К");
        translitEngine("l", "Л");
        translitEngine("m", "М");
        translitEngine("n", "Н");
        translitEngine("o", "О");
        translitEngine("p", "П");
        translitEngine("r", "Р");
        translitEngine("i", "И");
        translitEngine("v", "В");
        translitOutput = translitInput;
    }

    public void translitEngine(String first, String second){
        if (translitInput.contains(first)){
            translitInput = translitInput.replace(first, second);
        }
        else{
            translitOutput = translitInput;
        }
    }
}
