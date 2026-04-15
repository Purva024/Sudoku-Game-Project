<h1>Sudoku Game in Java</h1>
<p>This project is a desktop-based Sudoku game developed using Java programming language and Swing for graphical 
user interface (GUI). It consists of a 9×9 grid divided into 3×3 sub-grids.
The goal of the game is to fill the grid with numbers from 1 to 9 such that: 
  <ul>
    <li>Each number appears only once in each row.</li>
    <li>Each number appears only once in each column.</li>
    <li>Each number appears only once in each 3×3 box.</li>
  </ul>
</p>
<h3>Functioning</h3>
  <ul>
    <li>Numbers are added cell by cell if they satisfy the row constraint, column constraint and 3x3 grid constraint.</li>
    <li>A valid solution is generated and stored.</li>
    <li>After generating the complete solution, cells are removed randomly. The number of clues depends on difficulty:
      <ul>
        <li>Easy – 36 clues</li>
        <li>Medium – 30 clues</li>
        <li>Hard – 24 clues</li>
      </ul>
    <li>When a user inputs a number, the value is compared with solution and status updated accordingly:</li>
      <ul>
        <li>Correct → Black text</li>
        <li>Incorrect → Red text</li>
      </ul>
    <li>The system counts correctly filled cells. If correct count = 81, the system displays congratulatory dialog box.</li>
  </ul>
<h3>Features</h3>
  <ul>
    <li>Includes difficulty levels: Easy, Medium, Hard</li>
    <li>Real-time validation</li>
    <li>Disable number buttons when all 9 correct values are used.</li>
    <li>Clear all option for erasing all the cells and Erase option for erasing a particular cell.</li>
    <li>When a pre-defined cell is highlighted, the cells containing the same number are highlighted</li>
  </ul>
