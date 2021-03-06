package com.myproject.tictactoe.ai;

/**
 * Реализует поведение бота, описанной в интерфейсе <code>Bot</code> и некоторые вспомогательные методы.
 */
public abstract class BotImpl implements Bot {
    /** Константа, соответствующая крестику на игровом поле. */
    protected final static char CROSS = '×';
    /** Константа, соответствующая нолику на игровом поле. */
    protected final static char ZERO = '￮';
    /** Константа, соответствующая пустой ячейке на игровом поле. */
    protected final static char EMPTY = ' ';
    /** Регулярные выражения, соответствующие выигрышным комбинациям на игровом поле. */
    protected final static String[] WINS = {
            "@@@......", "...@@@...", "......@@@",
            "@..@..@..", ".@..@..@.", "..@..@..@",
            "@...@...@", "..@.@.@..",};
    /**
     * Символ бота.
     * @see BotImpl#CROSS
     * @see BotImpl#ZERO
     */
    protected char mySymbol;
    /**
     * Символ игрока.
     * @see BotImpl#CROSS
     * @see BotImpl#ZERO
     */
    protected char enemySymbol;

    /** Стандартный конструктор. */
    public BotImpl() {
    }

    /**
     * На основании предоставленного в виде строки игрового поля устанавливает свой символ,
     * определяет текущую ситуацию на поле, замеряет время, за которое конкретный алгоритм
     * определяет место для хода и возвращает объект, соответствующий новому состоянию поля.
     *
     * @param field игровое поле в виде строки
     * @return состояние игрового поля после хода бота
     * @see GameState
     */
    public GameState play(String field) {
        int index;
        mySymbol = findMySymbol(field);
        enemySymbol = (mySymbol == CROSS) ? ZERO : CROSS;
        GameState.GameStatus gameStatus = getGameStatus(field);

        if (gameStatus != GameState.GameStatus.CONTINUE) {
            index = getWinningIndex(field);
            return new GameState(gameStatus, -1, index);
        }

        long startTime = System.nanoTime();

        int place = getPlaceToStep(field);

        long endTime = System.nanoTime();
        System.out.printf("%s found place to step in %d ms%n",
                this.getClass().getCanonicalName(), (endTime - startTime) / 1000000);

        String newField = doStep(field, mySymbol, place);
        gameStatus = getGameStatus(newField);
        index = getWinningIndex(newField);

        return new GameState(gameStatus, place, index);
    }

    public abstract int getPlaceToStep(String field);

    /**
     * Если на поле не осталось свободных ячеек для совершения хода возвращает <code>true</code>,
     * иначе - <code>false</code>.
     *
     * @param field игровое поле в виде строки
     */
    private static boolean noEmptyPlaces(String field) {
        for (int i = 0; i < field.length(); i++) {
            if (field.charAt(i) == EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, выиграл ли заданный символ игру.
     *
     * @param field игровое поле в виде строки
     * @param symbol символ для проверки
     */
    protected static boolean winning(String field, char symbol) {
        for (String pattern : WINS) {
            if (field.matches(pattern.replace('@', symbol)))
                return true;
        }
        return false;
    }

    /**
     * Определяет выигрышную комбинацию на поле, если игра окончена
     *
     * @param field игровое поле в виде строки
     */
    private int getWinningIndex(String field) {
        for (int i = 0; i < WINS.length; i++) {
            String pattern = WINS[i];
            if (field.matches(pattern.replace('@', mySymbol)) || field.matches(pattern.replace('@', enemySymbol)))
                return i;
        }
        return -1;
    }

    /**
     * Определяет символ бота по текущему игровому полю.
     *
     * @param field игровое поле в виде строки
     * @return игровой символ бота
     */
    private static char findMySymbol(String field) {
        int balance = 0;
        for (int i = 0; i < field.length(); i++) {
            switch (field.charAt(i)) {
                case ZERO:
                    balance--;
                    break;
                case CROSS:
                    balance++;
                    break;
            }
        }
        if (balance <= 0) {
            return CROSS;
        }
        return ZERO;
    }

    /**
     * Определяет состояние игры по текущему игровому полю.
     *
     * @param field игровое поле в виде строки
     * @see GameState.GameStatus
     */
    private GameState.GameStatus getGameStatus(String field) {
        if (noEmptyPlaces(field)) {
            return GameState.GameStatus.DRAW;
        } else if (winning(field, mySymbol)) {
            return GameState.GameStatus.LOSS;
        } else if (winning(field, enemySymbol)) {
            return GameState.GameStatus.WIN;
        }
        return GameState.GameStatus.CONTINUE;
    }

    /**
     * Делает ход определенным символом на заданное место.
     *
     * @param field игровое поле в виде строки
     * @param symbol символ, которым необходимо сделать ход
     * @param place место для хода
     * @return игровое поле после хода бота
     */
    protected String doStep(String field, char symbol, int place) {
        return field.substring(0, place) + symbol + field.substring(place + 1);
    }
}