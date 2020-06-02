<template>
  <div>
    <h3>{{user.name}}</h3>
    <v-autocomplete v-if="!readonly"
      v-model="user.roles"
      :items="ROLES"
      :readonly="readonly"
      :chips="!readonly"
      :filter="filterRoles"
      :clearable="!readonly"
      @paste="onPaste"
      label="Roles"
      multiple deletable-chips
    ></v-autocomplete>
    <v-combobox v-if="readonly"
      v-model="user.roles"
      :items="ROLES"
      :readonly="readonly"
      :chips="!readonly"
      :filter="filterRoles"
      :clearable="!readonly"
      @paste="onPaste"
      label="Roles"
      multiple deletable-chips
    ></v-combobox>
  </div>
</template>

<script>

import { mapGetters } from 'vuex';

export default {
  props: {
    user: {
      type: Object,
      required: true,
    },
    readonly: {
      type: Boolean,
      default: false,
    }
  },
  computed: {
    ...mapGetters(['ROLES']),
  },
  methods: {
    onPaste(evt) {
      const pastedTxt = evt.clipboardData.getData('Text');
      const pastedTerms = pastedTxt.split(",").map(s => s.trim());
      console.log(pastedTerms);
      console.log('support copy paste... maybe use combobox?');
      return false;
    },
    filterRoles(item, queryText, itemText) {
      const queries = queryText.split(",").map(s => s.trim().toLowerCase());
      const itemStr = itemText.toLowerCase();
      for(const query of queries) {
        if(itemStr.includes(query))
          return true;
      }
      return false;
    },
  }
};
</script>

<style scoped>
  
</style>
