import javax.swing.*; 
import javax.swing.border.Border; 
import javax.swing.border.LineBorder; 
import java.awt.*; 
import java.awt.event.*; 
import java.util.*; 
public class SudokuApp { 
public static void main(String[] args) { 
        SwingUtilities.invokeLater(() -> new SudokuFrame().setVisible(true)); 
    } 
} 
 
class SudokuFrame extends JFrame { 
    private final SudokuGenerator generator = new SudokuGenerator(); 
    private int[][] solution; 
    private int[][] puzzle; 
    private final CellField[][] cells = new CellField[9][9]; 
    private final JLabel status = new JLabel(" "); 
    private final JButton[] numberButtons = new JButton[10]; 
    private int selectedNumber = 0; 
    private CellField selectedCell = null; 
    private Difficulty difficulty = Difficulty.EASY; 
    private boolean solvedShown = false; 
 
    private final Color GRID_BG = new Color(255, 255, 255); 
    private final Color HIGHLIGHT = new Color(73, 136, 196); 
    private final Color SELECTED = new Color(210, 230, 255); 
    private final Color GIVEN_BG = new Color(255, 255, 255); 
    private final Color ERROR_RED = new Color(200, 40, 40); 
 
    SudokuFrame() { 
        setTitle("Sudoku"); 
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
        setSize(1000, 640); 
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout(12, 12)); 
 
        add(buildTopBar(), BorderLayout.NORTH); 
        add(buildBoard(), BorderLayout.CENTER); 
        add(buildSidePanel(), BorderLayout.EAST); 
        add(buildStatus(), BorderLayout.SOUTH); 
 
        newGame(); 
    } 
 
    private JComponent buildTopBar() { 
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8)); 
        top.add(new JLabel("Difficulty:")); 
 
        JComboBox<Difficulty> diffBox = new JComboBox<>(Difficulty.values()); 
        diffBox.setSelectedItem(difficulty); 
        diffBox.addActionListener(e -> { 
            difficulty = (Difficulty) diffBox.getSelectedItem(); 
            newGame(); 
        }); 
        top.add(diffBox); 
 
        JButton newGame = new JButton("New Game"); 
        newGame.addActionListener(e -> this.newGame()); 
        top.add(newGame); 
 
        return top; 
    } 
 
    private JComponent buildBoard() { 
        JPanel grid = new JPanel(new GridLayout(9, 9)); 
        grid.setBorder(new LineBorder(Color.DARK_GRAY, 2)); 
        grid.setBackground(GRID_BG); 
 
        Font font = new Font("SansSerif", Font.BOLD, 22); 
 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = new CellField(r, c); 
                cell.setHorizontalAlignment(JTextField.CENTER); 
                cell.setFont(font); 
                cell.setBackground(Color.WHITE); 
                cell.setBorder(makeCellBorder(r, c)); 
                cell.setPreferredSize(new Dimension(55, 55)); 
 
                cell.addMouseListener(new MouseAdapter() { 
                    @Override public void mousePressed(MouseEvent e) { 
                        setSelectedCell(cell); 
                    } 
                }); 
 
                cell.addKeyListener(new KeyAdapter() { 
                    @Override public void keyTyped(KeyEvent e) { 
                        char ch = e.getKeyChar(); 
                        if (ch < '1' || ch > '9') { 
                            if (ch == KeyEvent.VK_BACK_SPACE || ch == KeyEvent.VK_DELETE) { 
                                eraseCell(cell); 
                            } 
                            e.consume(); 
                            return; 
                        } 
                        if (cell.isGiven) { 
                            e.consume(); 
                            return; 
                        } 
                        int val = ch - '0'; 
                        setCellValue(cell, val); 
                        e.consume(); 
                    } 
                }); 
 
                cells[r][c] = cell; 
                grid.add(cell); 
            } 
        } 
 
        return grid; 
    } 
 
    private JComponent buildSidePanel() { 
        JPanel side = new JPanel(); 
        side.setLayout(new BorderLayout(0, 8)); 
 
        JPanel numbers = new JPanel(new GridLayout(5, 2, 8, 8)); 
        numbers.setBorder(BorderFactory.createTitledBorder("Numbers")); 
 
        for (int i = 1; i <= 9; i++) { 
            int val = i; 
            JButton btn = new JButton(String.valueOf(i)); 
            btn.setPreferredSize(new Dimension(60, 45)); 
            numberButtons[val] = btn; 
            numbers.add(btn); 
            btn.addActionListener(e -> { 
                selectedNumber = val; 
                applySelectedNumber(); 
            }); 
 
        } 
 
        JButton clearAll = new JButton("Clear All"); 
        clearAll.addActionListener(e -> clearAllUserEntries()); 
 
        JButton erase = new JButton("Erase"); 
        erase.addActionListener(e -> eraseSelectedCell()); 
 
        JPanel actions = new JPanel(new GridLayout(2, 1, 8, 8)); 
        actions.add(clearAll); 
        actions.add(erase); 
 
        side.add(numbers, BorderLayout.NORTH); 
        side.add(actions, BorderLayout.SOUTH); 
 
        return side; 
    } 
 
    private JComponent buildStatus() { 
        JPanel panel = new JPanel(new BorderLayout()); 
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); 
        status.setText(" "); 
        panel.add(status, BorderLayout.WEST); 
        return panel; 
    } 
 
    private Border makeCellBorder(int r, int c) { 
        int top = (r % 3 == 0) ? 2 : 1; 
        int left = (c % 3 == 0) ? 2 : 1; 
        int bottom = (r == 8) ? 2 : 1; 
        int right = (c == 8) ? 2 : 1; 
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.DARK_GRAY); 
    } 
       
    private void newGame() { 
        solution = generator.generateSolution(); 
        puzzle = generator.makePuzzle(solution, difficulty); 
 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                int val = puzzle[r][c]; 
                if (val == 0) { 
                    cell.setText(""); 
                    cell.setEditable(true); 
                    cell.isGiven = false; 
                    cell.setForeground(Color.BLACK); 
                    cell.setBackground(Color.WHITE); 
                } else { 
                    cell.setText(String.valueOf(val)); 
                    cell.setEditable(false); 
                    cell.isGiven = true; 
                    cell.setForeground(Color.BLACK); 
                    cell.setBackground(GIVEN_BG); 
                } 
            } 
        } 
        status.setText("New game: " + difficulty.label); 
        selectedNumber = 0; 
        selectedCell = null; 
        solvedShown = false; 
        clearHighlights(); 
        updateCountsAndButtons(); 
    } 
 
    private void setSelectedCell(CellField cell) { 
        clearHighlights(); 
        cell.setBackground(SELECTED); 
        highlightNumberIfAny(cell.getValue()); 
        selectedCell = cell; 
        cell.requestFocusInWindow(); 
    } 
 
    private void clearHighlights() { 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                if (cell.isGiven) { 
                    cell.setBackground(GIVEN_BG); 
                } else { 
                    cell.setBackground(Color.WHITE); 
                } 
            } 
        } 
    } 
 
    private void highlightNumberIfAny(int number) { 
        if (number <= 0) return; 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                if (cell.getValue() == number) { 
                    if (cell.isGiven) { 
                        cell.setBackground(HIGHLIGHT); 
                    } else { 
                        cell.setBackground(new Color(255, 250, 210)); 
                    } 
                } 
            } 
        } 
    } 
 
    private void setCellValue(CellField cell, int val) { 
        cell.setText(String.valueOf(val)); 
        int r = cell.row; 
        int c = cell.col; 
 
        if (solution[r][c] == val) { 
            cell.setForeground(Color.BLACK); 
            status.setText("Looks good."); 
        } else { 
            cell.setForeground(ERROR_RED); 
            status.setText("That number is not correct."); 
        } 
        clearHighlights(); 
        highlightNumberIfAny(val); 
        updateCountsAndButtons(); 
        checkSolved(); 
    } 
 
    private void applySelectedNumber() { 
        CellField cell = selectedCell; 
        if (cell == null || cell.isGiven) return; 
        if (selectedNumber <= 0) return; 
        setCellValue(cell, selectedNumber); 
    } 
 
    private void eraseSelectedCell() { 
        CellField cell = selectedCell; 
        if (cell == null) return; 
        eraseCell(cell); 
    } 
 
    private void eraseCell(CellField cell) { 
        if (cell.isGiven) return; 
        cell.setText(""); 
        cell.setForeground(Color.BLACK); 
        status.setText("Cleared."); 
        clearHighlights(); 
        updateCountsAndButtons(); 
    } 
 
    private void clearAllUserEntries() { 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                if (!cell.isGiven) { 
                    cell.setText(""); 
                    cell.setForeground(Color.BLACK); 
                } 
            } 
        } 
        status.setText("Cleared all user entries."); 
        selectedNumber = 0; 
        clearHighlights(); 
        updateCountsAndButtons(); 
        solvedShown = false; 
    } 
 
    private void updateCountsAndButtons() { 
        int[] counts = new int[10]; 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                int val = cell.getValue(); 
                if (val > 0 && solution[r][c] == val) { 
                    counts[val]++; 
                } 
            } 
        } 
        for (int i = 1; i <= 9; i++) { 
            JButton btn = numberButtons[i]; 
            if (btn != null) { 
                btn.setEnabled(counts[i] < 9); 
            } 
        } 
    } 
 
    private void checkSolved() { 
        if (solvedShown) return; 
        int correct = 0; 
        for (int r = 0; r < 9; r++) { 
            for (int c = 0; c < 9; c++) { 
                CellField cell = cells[r][c]; 
                int val = cell.getValue(); 
                if (val > 0 && solution[r][c] == val) { 
                    correct++; 
                } 
            } 
        } 
        if (correct == 81) { 
            solvedShown = true; 
            status.setText("Congratulations! You solved the puzzle."); 
            JOptionPane.showMessageDialog( 
                this, 
                "Congratulations! You solved the puzzle.", 
                "Sudoku", 
                JOptionPane.INFORMATION_MESSAGE 
            ); 
        } 
    } 
} 
 
class CellField extends JTextField { 
    final int row; 
    final int col; 
    boolean isGiven = false; 
 
    CellField(int row, int col) { 
        super(1); 
        this.row = row; 
        this.col = col; 
        setFocusable(true); 
    } 
 
    int getValue() { 
        String t = getText().trim(); 
        if (t.isEmpty()) return 0; 
        try { 
            return Integer.parseInt(t); 
        } catch (NumberFormatException e) { 
            return 0; 
        } 
    } 
} 
 
enum Difficulty { 
    EASY("Easy", 36), 
    MEDIUM("Medium", 30), 
    HARD("Hard", 24); 
 
    final String label; 
    final int clues; 
 
    Difficulty(String label, int clues) { 
        this.label = label; 
        this.clues = clues; 
    } 
 
    @Override public String toString() { return label; } 
} 
 
class SudokuGenerator { 
    private final Random rnd = new Random(); 
 
    int[][] generateSolution() { 
        int[][] board = new int[9][9]; 
        fillBoard(board, 0, 0); 
        return board; 
    } 
 
    private boolean fillBoard(int[][] board, int r, int c) { 
        if (r == 9) return true; 
        int nextR = (c == 8) ? r + 1 : r; 
        int nextC = (c + 1) % 9; 
 
        int[] nums = shuffledNumbers(); 
        for (int num : nums) { 
            if (isValid(board, r, c, num)) { 
                board[r][c] = num; 
                if (fillBoard(board, nextR, nextC)) return true; 
                board[r][c] = 0; 
            } 
        } 
        return false; 
    } 
 
    int[][] makePuzzle(int[][] solution, Difficulty diff) { 
        int[][] puzzle = copy(solution); 
        int cellsToRemove = 81 - diff.clues; 
 
        while (cellsToRemove > 0) { 
            int r = rnd.nextInt(9); 
            int c = rnd.nextInt(9); 
            if (puzzle[r][c] != 0) { 
                puzzle[r][c] = 0; 
                cellsToRemove--; 
            } 
        } 
        return puzzle; 
    } 
 
    private int[] shuffledNumbers() { 
        int[] nums = new int[9]; 
        for (int i = 0; i < 9; i++) nums[i] = i + 1; 
        for (int i = nums.length - 1; i > 0; i--) { 
            int j = rnd.nextInt(i + 1); 
            int tmp = nums[i]; 
            nums[i] = nums[j]; 
            nums[j] = tmp; 
        } 
        return nums; 
    } 
 
    private boolean isValid(int[][] board, int r, int c, int num) { 
        for (int i = 0; i < 9; i++) { 
            if (board[r][i] == num) return false; 
            if (board[i][c] == num) return false; 
        } 
        int boxRow = (r / 3) * 3; 
        int boxCol = (c / 3) * 3; 
        for (int i = 0; i < 3; i++) { 
            for (int j = 0; j < 3; j++) { 
                if (board[boxRow + i][boxCol + j] == num) return false; 
            } 
        } 
        return true; 
    } 
 
    private int[][] copy(int[][] src) { 
        int[][] out = new int[9][9]; 
        for (int i = 0; i < 9; i++) { 
            System.arraycopy(src[i], 0, out[i], 0, 9); 
        } 
        return out; 
    } 
}