import java.util.Random;

/*public class Carstensen_Lab_16{
    static Deck deck = new Deck();
    public static void main(String[] args){
        deck.reset();
        deck.shuffle();
        System.out.println(deck.draw().getName());
        System.out.println(deck.draw().getName());
        System.out.println(deck.draw().getName());
        System.out.println(deck.draw().getName());
    }
}*/
import javax.swing.JFrame;

enum SUIT 
{ 
    HEARTS, SPADES, CLUBS, DIAMONDS;
}

class Carstensen_Lab_16 extends JFrame{
    private int screenWidth = 900;
    private int screenHeight = 950;
    
    public static Carstensen_Lab_16 ex;
    public Carstensen_Lab_16(){
        initUI();
    }
    private void initUI(){
        add(new Game(screenWidth, screenHeight, this));
        setSize(screenWidth, screenHeight);
        setTitle("Solitaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    public static void main(String[] args){
        ex = new Carstensen_Lab_16();
        ex.setVisible(true); 
    }
    private static final long serialVersionUID = 1L;
}
class Deck {
    Card[] cards = new Card[52];
    int currentIndex = 0;
    int length = 0;
    public void shuffle(){
        for(int i = 0; i < length; i++){
            int newPosition = new Random().nextInt(length);
            Card oldCard = cards[i];
            cards[i] = cards[newPosition];
            cards[newPosition] = oldCard;
        }
    }
    public void reset(){
        for(int i = 0; i < 4; i++){
            for(int j = 1; j <= 13; j++){
                add(new Card(j, SUIT.values()[i]));
            }
        }
    }
    public void format(){
        currentIndex = 0;
        length = 0;
    }
    public void add(Card card){
        if(currentIndex < cards.length){
            cards[currentIndex] = card;
            currentIndex++;
            length++;
        }
    }
    public int getLength(){
        return length;
    }
    public Card draw(){
        if(currentIndex > 0){
            currentIndex--;
            length--;
            Card card = cards[currentIndex];
            cards[currentIndex] = new Card();
            return card;
        }else{
            return new Card();
        }
    }
}
class Card {
    int number;
    SUIT suit;
    public boolean visible = false;
    public boolean taken = false;
    public Card(){
        number = 1;
        suit = SUIT.DIAMONDS;
    }
    public boolean getIsBlack(){
        return (suit == SUIT.CLUBS || suit == SUIT.SPADES);
    }
    public String decapitalize(String string){
        String newString = "";
        for(int i = 0; i < string.length(); i++){
            if(i == 0)
                newString += string.charAt(i);
            else
                newString += string.toLowerCase().charAt(i);
        }
        return newString;
    }
    public String getName(){
        String name = Integer.toString(number);
        if(number == 13)
            name = "King";
        else if(number == 12)
            name = "Queen";
        else if(number == 11)
            name = "Jack";

        return name + " of " + decapitalize(suit.toString());
    }
    public Card(int newNumber, SUIT newSuit){
        number = newNumber;
        suit = newSuit;
    }
    public String getSuit(){
        switch(suit){
            case SPADES:
                return "\u2660";
            case HEARTS:
                return "\u2661";
            case DIAMONDS:
                return "\u2662";
            case CLUBS:
                return "\u2663";
            default:
                return "E";
        }
    }
}