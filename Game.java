//It turns out, we need a lot of classes/functions/methods for this to work
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.MouseInfo;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.FontMetrics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.MouseInputListener;

public class Game extends JPanel implements ActionListener {
    Deck deck = new Deck();
    Deck secondaryDeck = new Deck();
    //All global integers
    Column[] board = new Column[7];
    private final int FPS_CAP = 40;
    private int screenHeight;
    private int spaceBetween;
    private int screenWidth;
    private int circleSize;
    int clickedColumn = 0;
    int currentColumn = 0;
    int currentItem = -1;
    int currentPosX = 0;
    int currentPosY = 0;
    int setKeyCode = -1;
    int currentRow = 0;
    int escapeInt = 27;
    int counter = 0;
    int rightKey = 39;
    int spaceKey = 32;
    int leftKey = 37;
    int losses = 0;
    int wins = 0;
    int key = -1;
    int escapeKeyCode;

    //All global colors
    final Color player2 = Color.yellow;
    final Color blank = Color.white;
    final Color player1 = Color.red;

    //All global booleans
    boolean displayingStatistics = false;
    boolean loadingOrSavingGame = false;
    boolean changingGamemode = false;
    boolean settingsOpen = false;
    boolean singlePlayer = false;
    boolean managedWin = false;
    boolean escapeMenu = false;
    boolean mouseDown = false;
    boolean player_1 = false;
    boolean gameWon = false;
    boolean dragging = false;
    boolean showText = false;

    //All other global variables
    Font font = new Font("Serif", Font.BOLD, 24);
    Font winFont = new Font("Serif", Font.BOLD, 56);
    long last_time = System.nanoTime();
    String loadingOrSavingString = "";
    Dimension screenSize;
    double deltaTime = 0;
    private Timer timer;
    JFrame frame;
    Color backgroundColor = new Color(46,139,87);
    int toCarry = 1;

    Card card1 = new Card();
    Card card2 = new Card();
    Card card3 = new Card();

    Column[] aces = new Column[4];

    //Removes all chips from the game
    void resetGame(){
        for(int j = 0; j < board.length; j++){
            board[j] = new Column();
        }
        deck.reset();
        deck.shuffle();
        secondaryDeck.format();
        for(int i = 0; i < board.length; i++){
            board[i].reset();
            for(int j = 0; j < i + 1; j++){
                board[i].append(deck.draw());
            }
            board[i].setLastCardVisible();
        }
        for(int j = 0; j < aces.length; j++){
            aces[j] = new Column();
            aces[j].reversed = true;
        }
    }

    //Gets delta time (Used to make button animations smooth)
    void calculateFps(){
        long time = System.nanoTime();
        deltaTime = (int) ((time - last_time) / 1000000);
        last_time = time;
        deltaTime /= 1000;
    }

    //Gets the height and width of the window
    void calculateDismensions(){
        screenSize = frame.getBounds().getSize();
    }

    //Draws our circles and text to the screen
    void drawBoard(Graphics g){
        //Get deltaTime and window size
        calculateFps();
        calculateDismensions();
        //Determine where our chips are drawn
        int currentX = spaceBetween;
        int currentY = spaceBetween;
        g.setColor(Color.black);
        //FontMetrics metrics = g.getFontMetrics(font);
        currentX = spaceBetween;
        currentY = (int)(spaceBetween + circleSize * 1.5f);
        //drawCenteredCircle(g, (int)(currentX + (circleSize / 1.5f / 2)), currentY + circleSize / 2, circleSize / 2);
        drawCenteredCircle(g, (int)(20 + (circleSize / 1.5f / 2)), circleSize / 2, circleSize / 2);
        if(deck.length > 0){
            drawCard(g, 20, 20, (int)(circleSize / 1.5f), circleSize, new Card());
        }
        drawCenteredCircle(g, (int)(150 + (circleSize / 1.5f / 2)), circleSize / 2, circleSize / 2);
        if(card1.visible)
            drawCard(g, 150, 20, (int)(circleSize / 1.5f), circleSize, card1);
        drawCenteredCircle(g, (int)(250 + (circleSize / 1.5f / 2)), circleSize / 2, circleSize / 2);
        if(card2.visible)
            drawCard(g, 250, 20, (int)(circleSize / 1.5f), circleSize, card2);
        drawCenteredCircle(g, (int)(350 + (circleSize / 1.5f / 2)), circleSize / 2, circleSize / 2);
        if(card3.visible)
            drawCard(g, 350, 20, (int)(circleSize / 1.5f), circleSize, card3);

        for(int j = 0; j < aces.length; j++){
            if(aces[j].getLength() > 0){
                drawCard(g, 475 + (j * 100), 20, (int)(circleSize / 1.5f), circleSize, aces[j].getTopCard());
            }else{
                drawCenteredCircle(g, (int)(475 + (j * 100) + (circleSize / 1.5f / 2)), circleSize / 2, circleSize / 2);
            }
        }
        
        if(mouseDown){
            if(card1.visible && mouseOver(150, 20, (int)(circleSize / 1.5f), circleSize)){
                dragging = true;
                mouseDown = false;
                clickedColumn = -1;
                toCarry = 1;
            }else if(card2.visible && mouseOver(250, 20, (int)(circleSize / 1.5f), circleSize)){
                dragging = true;
                mouseDown = false;
                clickedColumn = -2;
                toCarry = 1;
            }else if(card3.visible && mouseOver(350, 20, (int)(circleSize / 1.5f), circleSize)){
                dragging = true;
                mouseDown = false;
                clickedColumn = -3;
                toCarry = 1;
            }
        }
        for(int j = 0; j < 25; j++){
            currentX = spaceBetween;
            for(int i = 0; i < board.length; i++){
                if(j == 0){
                    g.setColor(Color.gray);
                    drawCenteredCircle(g, (int)(currentX + (circleSize / 1.5f / 2)), currentY + circleSize / 2, circleSize / 2);
                }
                if(mouseOver(currentX, 0, (int)(circleSize / 1.5f), (int)screenSize.getHeight())){
                    currentColumn = i;
                }
                if(board[i].getLength() > j){
                    drawCard(g, currentX, currentY, (int)(circleSize / 1.5f), circleSize, board[i].getCard(j));
                }
                currentX += circleSize + spaceBetween;
            }
            currentY += circleSize / 3;
        }
        int newCarryOver = 0;
        currentY = (int)(spaceBetween + circleSize * 1.5f);
        for(int k = 0; k < board[currentColumn].getLength(); k++){
            if(mouseOver(0, currentY, (int)screenSize.getWidth(), circleSize / 3)){
                newCarryOver = board[currentColumn].getLength() - k;
                break;

            }
            currentY += circleSize / 3;
        }
        if(mouseDown){
            mouseDown = false;
            dragging = true;
            clickedColumn = currentColumn;
            toCarry = newCarryOver + 1;
            if(clickedColumn < 0) return;
            if(board[clickedColumn].getLength() == 0){
                dragging = false;
                toCarry = 1;
            }
        }
        if(dragging){
            int mX =  MouseInfo.getPointerInfo().getLocation().x - frame.getLocationOnScreen().x;
            int mY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocationOnScreen().y;
            Card card = new Card();
            if(clickedColumn >= 0){
                if(toCarry == 1)
                    card = board[clickedColumn].getTopCard();
                else{
                    int length = board[clickedColumn].getLength();
                    int Y = mY - 30;
                    for(int i = length - toCarry; i < length; i++){
                        drawCard(g, mX - 5, Y, (int)(circleSize / 1.5f), circleSize, board[clickedColumn].getCard(i));
                        Y += circleSize / 3;
                    }
                    return;
                }
            }else{
                if(clickedColumn == -1)
                    card = card1;
                else if(clickedColumn == -2)
                    card = card2;
                else
                    card = card3;
            }
            drawCard(g, mX - 5, mY - 30, (int)(circleSize / 1.5f), circleSize, card);
        }
        boolean won = true;
        for(int a = 0; a < aces.length; a++){
            if(aces[a].getLength() < 13){
                won = false;
            }
        }
        g.setColor(Color.red);
        if(won){
            g.setFont(winFont);
            g.drawString("You won the game!", (int)(screenSize.getWidth() / 2 - getFontMetrics(winFont).stringWidth("You won the game!") / 2), (int)(screenSize.getHeight() / 2 + getFontMetrics(winFont).getHeight() / 2));
            g.setFont(font);
        }
    }

    public void drawCenteredCircle(Graphics g, int x, int y, int r) {
        g.setColor(Color.gray);
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }
    
    //Utilized by the boxCollides function
    boolean collides(int x, int y, int r, int b, int x2, int y2, int r2, int b2) {//Algorithm to tell if two objects collide
        return !(r <= x2 || x > r2 || b <= y2 || y > b2);
    }

    //Takes in x & y positions of 2 objects, and their width & height 
    boolean boxCollides(int pos0, int pos1, int size0, int size1, int pos2, int pos3, int size2, int size3) {
        return collides(pos0, pos1,
            pos0 + size0, pos1 + size1,
            pos2, pos3,
            pos2 + size2, pos3 + size3);
    }

    //Test if mouse is over object
    boolean mouseOver(int x, int y, int width, int height){
        //Get mouse position
        int mX =  MouseInfo.getPointerInfo().getLocation().x - frame.getLocationOnScreen().x;
        int mY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocationOnScreen().y;
        //If the mouse intersects with the object
        if(boxCollides(mX - 1, mY - 1, 3, 3, x, y, width, height)){
            return true;
        }
        return false;
    }

    //Default Constructor (Don't use)
    public Game() {
        initBoard();
    }

    void getCard(){
        if(deck.getLength() == 0){
            while(secondaryDeck.getLength() > 0){
                deck.add(secondaryDeck.draw());
            }
            deck.shuffle();
            return;
        }
        if(!card2.visible){
            card2 = card1;
        }else if(!card3.visible){
            card3 = card2;
            card2 = card1;
        }else{
            if(card3.visible){
                secondaryDeck.add(card3);
            }
            card3 = card2;
            card2 = card1;
        }
        Card card = deck.draw();
        card.visible = true;
        card1 = card;
    }

    //Constructor setting dismensions and adding mouse listeners
    public Game(int sW, int sH, JFrame f){
        screenWidth = sW;
        screenHeight = sH;

        frame = f;
        frame.addMouseListener(new MouseInputListener(){
            @Override
            public void mouseMoved(MouseEvent e) {}
            @Override
            public void mouseDragged(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {
                boolean wasDragging = false;
                if(dragging)
                    wasDragging = true;
                dragging = false;
                //if(currentColumn == clickedColumn) return;
                if(wasDragging){
                    int currentX = 0;
                    for(int j = 0; j < aces.length; j++){
                        if(mouseOver(475 + (j * 100), 20, (int)(circleSize / 1.5f), circleSize)){
                            if(clickedColumn < 0){
                                Card card;
                                if(clickedColumn == -1)
                                    card = card1;
                                else if(clickedColumn == -2)
                                    card = card2;
                                else
                                    card = card3;
                                
                                if(aces[j].getLength() == 0 && card.number != 1) return;
                                if(aces[j].appendWithRules(card)){
                                    if(clickedColumn == -1)
                                        card1 = new Card();
                                    else if(clickedColumn == -2)
                                        card2 = new Card();
                                    else
                                        card3 = new Card();
                                    return;
                                }
                            }else{
                                if(aces[j].getLength() == 0 && board[clickedColumn].getTopCard().number != 1) return;
                                if(aces[j].appendWithRules(board[clickedColumn].getTopCard())){
                                    board[clickedColumn].removeCard();
                                    return;
                                }
                            }
                        }
                    }
                    if(toCarry == 1){
                        if(clickedColumn < 0){
                            if(clickedColumn == -1){
                                if(board[currentColumn].appendWithRules(card1)){
                                    card1 = new Card();
                                }
                            }else if(clickedColumn == -2){
                                if(board[currentColumn].appendWithRules(card2)){
                                    card2 = new Card();
                                }
                            }else if(clickedColumn == -3){
                                if(board[currentColumn].appendWithRules(card3)){
                                    card3 = new Card();
                                }
                            }
                        }else{
                            if(board[currentColumn].appendWithRules(board[clickedColumn].getTopCard())){
                                board[clickedColumn].removeCard();
                            }
                        }
                    }else{
                        Card[] cards = new Card[toCarry];
                        for(int i = 0; i < cards.length; i++){
                            cards[i] = board[clickedColumn].getTopCard2(i);
                            if(!cards[i].visible) return;
                        }
                        for(int j = cards.length - 1; j >= 0; j--){
                            if(board[currentColumn].appendWithRules(cards[j])){
                                board[clickedColumn].removeCard();
                            }else{
                                return;
                            }
                        }
                    }
                }
            }
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown = true;
            }
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseClicked(MouseEvent e) {
                if(mouseOver(20, 40, (int)(circleSize / 1.5f), circleSize)){
                    getCard();
                }
            }
        });
        initBoard();
    }

    //Called when the game starts
    private void initBoard() {
        resetGame();
        circleSize = screenWidth / 9;
        spaceBetween = screenWidth - (circleSize * 7);
        spaceBetween /= 9;
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(screenWidth, screenHeight));

        timer = new Timer((int)(1000 / FPS_CAP), this);
        timer.start();
    }

    void drawCard(Graphics g, int x, int y, int width, int height, Card card){
        int fontHeight = g.getFontMetrics(font).getHeight();
        g.setColor(Color.black);
        g.fillRect(x - 2, y - 2, width + 4, height + 4);
        g.setColor(Color.white);
        g.fillRect(x, y, width, height);
        if(card.visible){
            Color c = Color.RED;
            if(card.suit == SUIT.CLUBS || card.suit == SUIT.SPADES)
                c = Color.BLACK;
            g.setColor(c);
            String toDraw = card.getSuit();
            g.drawString(toDraw, (x + width / 2) - (getFontMetrics(font).stringWidth(toDraw) / 2), y + (int)(height / 2.5f) + fontHeight / 2);
            toDraw = Integer.toString(card.number);
            if(card.number > 10){
                if(card.number == 11)
                    toDraw = "Jack";
                else if(card.number == 12)
                    toDraw = "Queen";
                else
                    toDraw = "King";
            }

            g.drawString(toDraw, (x + width / 2) - (getFontMetrics(font).stringWidth(toDraw) / 2), y + (height / 10) + fontHeight / 2);
        }
    }

    //All Overrides are required from the parent class
    @Override
    public void paintComponent(Graphics g) {
        g.setFont(font);
        super.paintComponent(g);
        drawBoard(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    private static final long serialVersionUID = 1L;
}
class Column{
    int currentIndex = 0;
    public boolean reversed = false;
    Card[] cards = new Card[25];
    public void append(Card card){
        cards[currentIndex] = card;
        currentIndex++;
    }
    public boolean appendWithRules(Card card){
        card.visible = true;
        if(currentIndex == 0){
            append(card);
            return true;
        }
        if(reversed){
            if(card.suit.equals(cards[currentIndex - 1].suit)){
                if(card.number == cards[currentIndex - 1].number + 1){
                    append(card);
                    return true;
                }
            }
            return false;
        }
        if(card.getIsBlack() != cards[currentIndex - 1].getIsBlack()){
            if(card.number == cards[currentIndex - 1].number - 1){
                append(card);
                return true;
            }
        }
        return false;
    }
    public Card getTopCard(){
        return cards[currentIndex - 1];
    }
    public Card getTopCard2(int offset){
        return cards[currentIndex - 1 - offset];
    }
    public Column(){

    }
    public void appendCards(Card[] newCards){
        for(int i = 0; i < newCards.length; i++){
            cards[currentIndex] = newCards[i];
            currentIndex++;
            if(currentIndex == cards.length)
                break;
        }
    }
    public Card getCard(int index){
        return cards[Math.min(currentIndex - 1, index)];
    }
    public void reset(){
        currentIndex = 0;
    }
    public Card removeCard(){
        if(currentIndex <= 0) return new Card();
        Card newCard = cards[currentIndex - 1];
        currentIndex--;
        if(currentIndex <= 0) return newCard;
        cards[currentIndex - 1].visible = true;
        return newCard;
    }
    public int getLength(){
        return currentIndex;
    }
    public void setLastCardVisible(){
        cards[currentIndex - 1].visible = true;
    }
}