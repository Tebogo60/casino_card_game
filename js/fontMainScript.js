const gameCards = () => {
  for (let i = 0; i < suits.length; i++) {
    for (let j = 1; j <= 10; j++) {
      if (j === 1) {
        deckOfCards.innerHTML += `<div class="s_card ${suitsColors[i]}" id="A of ${suits[i]}"><span>A</span>${suitSvg[i]}</div>`;
      } else {
        deckOfCards.innerHTML += `<div class="s_card ${suitsColors[i]}" id="${j} of ${suits[i]}"><span>${j}</span>${suitSvg[i]}</div>`;
      }
    }
  }
};

const shuffle = () => {
  let randomIndex = Math.floor(Math.random());
};

gameCards();

for (let i = 0; i < deckOfCardsChildren.length; i++) {
  deckOfCardsChildren[i].style.top = `calc(50% - ${i / 2}px`;
}

let i = 0;

const deal = () => {
  if (i < 20) {
    if (i % 2 === 0) {
      deckOfCardsChildren[i].classList.add("card-top");
      deckOfCardsChildren[i].style.top = `50%`;
    } else {
      deckOfCardsChildren[i].classList.add("card-bottom");
      deckOfCardsChildren[i].style.top = `50%`;
    }
    i++;
    setTimeout(deal, 150);
  }

  if (i === 20) {
    for (let j = 0; j < 20; j++) {
      deckOfCardsChildren[j].style.opacity = "0";
    }
  }
};

deckOfCards.addEventListener("click", () => {
  deal();
});
