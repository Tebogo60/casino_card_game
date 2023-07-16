const suits = ["Hearts", "Diamonds", "Clubs", "Spades"];
const suitColors = ["color-red", "color-red", "color-black", "color-black"];

const arrayOfAllCards = [];
const arrayOfAllCardsElements = [];
let deck = document.querySelector("#deck");
let deckArray = [];

let shuffled = false;
let dealed = false;

let player1HoleCardsArray = [];
let player2HoleCardsArray = [];
let player1HoleCards = document.querySelector("#player1 .row");
let player2HoleCards = document.querySelector("#player2 .row");
const row = document.querySelectorAll(".row");

const card = document.querySelectorAll(".card");

let deckIndexes = [];
let randomIndexes = [];

const alert = document.querySelector(".alert");
const alertCard = document.querySelector(".alert-card");

let activeCard = "";
let preActiveCard = "";

let board = document.querySelector("#board");
const boardCards = [];

let cardActive = false;
let player1sTurn = true;

let currentRound = 0;
