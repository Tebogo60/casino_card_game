deckOfCards.addEventListener("click", () => {
  deal();
});

// this code changes the top and bottom section based on the number of cards

const changeWidth = () => {
  bottomCards.style.width = `calc(${
    40 * (bottomCards.childNodes.length + 1)
  }px + 5px)`;

  topCards.style.width = `calc(${
    40 * (topCards.childNodes.length + 1)
  }px + 5px)`;
  // console.log("topCards.childNodes.length :>> ", topCards.childNodes.length);
};
