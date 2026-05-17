const getPriceWithoutString = (itemPrice) => {
  return String(itemPrice).replace(/[^0-9]/g, '');
};

const emptyResult = () => ({
  item_img: undefined,
  item_name: undefined,
  item_price: undefined,
});

module.exports = {
  getPriceWithoutString,
  emptyResult,
};
