const getPriceWithoutString = (itemPrice) => {
  return String(itemPrice).replace(/[^0-9]/g, '');
};

const emptyResult = () => ({
  item_img: undefined,
  item_name: undefined,
  item_price: undefined,
});

const extractOgMeta = ($) => {
  const og = {};
  $('meta').each((_, el) => {
    const property = $(el).attr('property');
    if (typeof property !== 'string' || !property.startsWith('og:')) {
      return;
    }
    og[property.slice(3)] = $(el).attr('content');
  });
  return og;
};

const titleFallback = ($) => {
  const text = $('title').text();
  return text || undefined;
};

module.exports = {
  getPriceWithoutString,
  emptyResult,
  extractOgMeta,
  titleFallback,
};
