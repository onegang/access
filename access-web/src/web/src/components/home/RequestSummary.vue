<template>
  <div>
    <h3>Request Summary</h3>
    <div>Effective Date: {{REQUESTFORM.effectiveDate}}</div>
    <div v-if="REQUESTFORM.expiryDate">Expiry Date: {{REQUESTFORM.expiryDate}}</div>
    <div v-if="REQUESTFORM.comments">Comments: {{REQUESTFORM.comments}}</div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';

export default {
  computed: {
    ...mapGetters(['REQUESTFORM']),
  },
};
</script>

<style scoped>
  
</style>
