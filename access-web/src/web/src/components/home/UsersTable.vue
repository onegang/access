<template>
  <v-data-iterator
    :items="USERS"
    :items-per-page.sync="itemsPerPage"
    :page="page"
    :search="search"
    :sort-by="sortBy.toLowerCase()"
    :sort-desc="sortDesc"
    :custom-filter="filterUsers"
    hide-default-footer
  >
  <template v-slot:header>
    <v-text-field
      v-model="search"
      clearable
      outlined
      label="Find users..."
      prepend-inner-icon="mdi-magnify"
    ></v-text-field>
    </template>
    <template v-slot:default="props">
      <v-row>
        <v-col
          v-for="item in props.items"
          :key="item.name"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <v-card ripple @click="item.selected = !item.selected"
            class="app-table-card"
            v-bind:class="{'text--disabled': !item.active}">
            <v-card-title class="subheading" 
              v-bind:class="{'font-weight-bold': item.active}">
              <v-icon class="pr-2" v-if="item.selected">mdi-checkbox-marked-outline</v-icon>
              {{ item.name }}
              <div v-if="!item.active">(Deactivated)</div>
            </v-card-title>
            <v-divider></v-divider>

            <div class="app-table-role  px-5">
              {{ item.roles.join('\n') }}
            </div>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <template v-slot:footer>
      <v-row class="mt-2" align="center" justify="center">
        <span class="grey--text">Items per page</span>
        <v-menu offset-y>
          <template v-slot:activator="{ on }">
            <v-btn
              dark
              text
              color="primary"
              class="ml-2"
              v-on="on"
            >
              {{ itemsPerPage }}
              <v-icon>mdi-chevron-down</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="(number, index) in itemsPerPageArray"
              :key="index"
              @click="updateItemsPerPage(number)"
            >
              <v-list-item-title>{{ number }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-spacer></v-spacer>

        <span
          class="mr-4
          grey--text"
        >
          Page {{ page }} of {{ numberOfPages }}
        </span>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="mr-1"
          @click="formerPage"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="ml-1"
          @click="nextPage"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </v-row>
    </template>
  </v-data-iterator>
</template>

<script>

  import {mapGetters} from 'vuex';

  export default {
    mounted() {
      this.$store.dispatch('GET_USERS');
    },
    data() {
      return {
        itemsPerPageArray: [8, 16, 32],
        search: '',
        filter: {},
        sortDesc: false,
        page: 1,
        itemsPerPage: 8,
        sortBy: 'name',
        keys: [
          'Name',
          'Roles',
          'Active',
        ],
        items: [],
      };
    },
    computed: {
      numberOfPages() {
        return Math.ceil(this.USERS.length / this.itemsPerPage);
      },
      ...mapGetters(['USERS']),
    },
    methods: {
      nextPage() {
        if (this.page + 1 <= this.numberOfPages) this.page += 1;
      },
      formerPage() {
        if (this.page - 1 >= 1) this.page -= 1;
      },
      updateItemsPerPage(number) {
        this.itemsPerPage = number;
      },
      filterUsers(items, search) {
        if(search) {
          //we search by terms (separated by spaces)
          //also allow quoted terms and ignore case
          const searchTerms = search.match(/(?:[^\s"]+|"[^"]*")+/g).
            map(term => term.toLowerCase().replace(/^"(.*)"$/, '$1'));
          return items.filter(user => {
            //then we match by user name or roles
            const userValues = [user.name, ...user.roles].map(v => v.toLowerCase());
            for(const userValue of userValues) {
              for(const searchTerm of searchTerms) {
                if(userValue.indexOf(searchTerm) >= 0) {
                  return true;
                }
              }
            }
            return false;
          });
        } else {
          return items;
        }        
      },
    },
  };
</script>

<style scoped>
  .app-table-card {
    max-height: 200px;
  }
  .app-table-role {
    white-space: pre-line;
    height: 150px;
  }
</style>