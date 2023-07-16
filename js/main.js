const changeRowWidth = () => {
  let minWidth = 85;
  let minHeight = 120;

  row.forEach((row) => {
    if (row.childNodes.length === 0 || row.childNodes.length === 1) {
      row.parentElement.style.width = `${minWidth}px`;
    }
    if (row.childNodes.length > 1) {
      row.parentElement.style.width = `${
        minWidth + 40 * (row.childNodes.length - 1)
      }px`;
    }
  });

  if (deck.childNodes.length === 0 || deck.childNodes.length === 1) {
    deck.style.height = `${minHeight}px`;
  }

  if (deck.childNodes.length > 1) {
    deck.style.height = `${minHeight + 5 * (deck.childNodes.length - 1)}px`;
  }

  if (board.childNodes.length === 0 || board.childNodes.length === 1) {
    board.style.width = `${minWidth}px`;
  }

  if (board.childNodes.length > 1 && board.childNodes.length <= 5) {
    board.style.width = `${
      minWidth * board.childNodes.length + 10 * (board.childNodes.length - 1)
    }px`;
  }

  if (board.childNodes.length > 5) {
    board.style.height = `${minHeight * 2 + 10}px`;
  }
};
