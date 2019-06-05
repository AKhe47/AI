package v5.unit.type;


/**
 * 字符串元素
 * */
public class Word extends Element {

    private String content;

    public Word(String content){
        this.content = new String(content);
    }

    public Word(Word word){
        this(word.getContent());
    }

    @Override
    public boolean equals(Element element) {
        if(element.getClass().getSimpleName().equals("Word")){
            if(content.equals(((Word)element).getContent())){
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public Element addLast(Element element) {
        if(element.getClass().getSimpleName().equals("Word")){
            return new Word(content + ((Word)element).getContent());
        }
        return null;
    }

    @Override
    public Element sub(int begin, int size) {
        return new Word(content.substring(begin, begin + size));
    }

    @Override
    public int size() {
        return content.length();
    }

    @Override
    public Element getCopy() {
        return new Word(this);
    }

    private String symbolList = ",<.>/?;:\'\"[{]}~!@#$%^&*()_+=-《》，。/？；：‘“’”】}【{！@#￥%……&*（）-——=+、|";

    @Override
    public boolean isSpecial() {
        for(int j = 0; j < symbolList.length(); j++){
            if((symbolList.charAt(j) + "").equals(content)){
                return true;
            }
        }
        return false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toString(){
        return content;
    }

}
