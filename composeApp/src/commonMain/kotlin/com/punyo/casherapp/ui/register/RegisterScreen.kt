package com.punyo.casherapp.ui.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.extensions.format
import com.punyo.casherapp.ui.component.ResponsiveGrid
import kotlinx.coroutines.flow.Flow
import meijopharmcasherapp.composeapp.generated.resources.Res
import meijopharmcasherapp.composeapp.generated.resources.button_cancel
import meijopharmcasherapp.composeapp.generated.resources.button_confirm
import meijopharmcasherapp.composeapp.generated.resources.content_desc_menu
import meijopharmcasherapp.composeapp.generated.resources.content_desc_search
import meijopharmcasherapp.composeapp.generated.resources.dialog_transaction_confirm
import meijopharmcasherapp.composeapp.generated.resources.input_mode_camera
import meijopharmcasherapp.composeapp.generated.resources.input_mode_search
import meijopharmcasherapp.composeapp.generated.resources.label_cart
import meijopharmcasherapp.composeapp.generated.resources.label_discount
import meijopharmcasherapp.composeapp.generated.resources.label_discount_amount
import meijopharmcasherapp.composeapp.generated.resources.label_discount_prefix
import meijopharmcasherapp.composeapp.generated.resources.label_items_count
import meijopharmcasherapp.composeapp.generated.resources.label_price_quantity
import meijopharmcasherapp.composeapp.generated.resources.label_product_count
import meijopharmcasherapp.composeapp.generated.resources.label_product_count_prefix
import meijopharmcasherapp.composeapp.generated.resources.label_subtotal
import meijopharmcasherapp.composeapp.generated.resources.label_total
import meijopharmcasherapp.composeapp.generated.resources.label_total_amount
import meijopharmcasherapp.composeapp.generated.resources.menu_apply_discount
import meijopharmcasherapp.composeapp.generated.resources.menu_change_quantity
import meijopharmcasherapp.composeapp.generated.resources.menu_delete
import meijopharmcasherapp.composeapp.generated.resources.message_confirm_transaction
import meijopharmcasherapp.composeapp.generated.resources.placeholder_search_product
import meijopharmcasherapp.composeapp.generated.resources.sort_label
import meijopharmcasherapp.composeapp.generated.resources.sort_name_asc
import meijopharmcasherapp.composeapp.generated.resources.sort_name_desc
import meijopharmcasherapp.composeapp.generated.resources.sort_price_asc
import meijopharmcasherapp.composeapp.generated.resources.sort_price_desc
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(registerScreenViewModel: RegisterScreenViewModel = koinInject()) {
    val uiState by registerScreenViewModel.uiState.collectAsState()
    val searchQuery by registerScreenViewModel.searchQuery.collectAsState()
    val sortOption by registerScreenViewModel.sortOption.collectAsState()

    var quantityDialogState by remember { mutableStateOf<QuantityDialogState?>(null) }
    var discountDialogState by remember { mutableStateOf<DiscountDialogState?>(null) }
    var showDialogState by remember { mutableStateOf(false) }

    ResponsiveGrid(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        maxColumns = 3,
    ) { _, maxHeight, gridColumns ->
        item(
            span = {
                GridItemSpan(if (gridColumns == 3) 2 else gridColumns)
            },
        ) {
            Column(modifier = Modifier.height(maxHeight)) {
                InputModeSelector(
                    selectedMode = uiState.inputMode,
                    onModeSelected = registerScreenViewModel::setInputMode,
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (uiState.inputMode) {
                    InputMode.CAMERA -> CameraArea(
                        modifier = Modifier.fillMaxSize(),
                        onBarcodeScanned = registerScreenViewModel::onBarcodeScanned,
                    )

                    InputMode.SEARCH -> ProductSearchArea(
                        modifier = Modifier.fillMaxSize(),
                        searchQuery = searchQuery,
                        sortOption = sortOption,
                        searchResults = registerScreenViewModel.pagingDataFlow,
                        onSearchQueryChange = registerScreenViewModel::updateSearchQuery,
                        onSortOptionChange = registerScreenViewModel::updateSortOption,
                        onProductSelected = registerScreenViewModel::addProductToCart,
                    )
                }
            }
        }
        item(
            span = {
                GridItemSpan(if (gridColumns == 3) 1 else gridColumns)
            },
        ) {
            Row(modifier = Modifier.height(maxHeight)) {
                if (gridColumns == 3) {
                    VerticalDivider(
                        modifier = Modifier
                            .padding(end = 16.dp),
                    )
                }
                RightPanel(
                    modifier = Modifier.height(maxHeight),
                    uiState = uiState,
                    onQuantityUpdate = registerScreenViewModel::updateCartItemQuantity,
                    onRemoveItem = registerScreenViewModel::removeCartItem,
                    onApplyDiscount = registerScreenViewModel::applyDiscount,
                    onShowQuantityDialog = { index, cartItem ->
                        quantityDialogState = QuantityDialogState(index, cartItem)
                    },
                    onShowDiscountDialog = { index, cartItem ->
                        discountDialogState = DiscountDialogState(index, cartItem)
                    },
                    onShowDeleteDialog = { index, _ ->
                        registerScreenViewModel.removeCartItem(index)
                    },
                    onShowConfirmDialog = {
                        showDialogState = true
                    },
                )
            }
        }
    }

    quantityDialogState?.let { state ->
        QuantityUpdateDialog(
            productName = state.cartItem.product.name,
            currentQuantity = state.cartItem.quantity,
            onConfirm = { newQuantity ->
                registerScreenViewModel.updateCartItemQuantity(state.index, newQuantity)
                quantityDialogState = null
            },
            onDismiss = { quantityDialogState = null },
        )
    }

    discountDialogState?.let { state ->
        DiscountDialog(
            productName = state.cartItem.product.name,
            currentDiscountPercent = state.cartItem.discountPercent,
            onConfirm = { discountPercent ->
                registerScreenViewModel.applyItemDiscount(state.index, discountPercent)
                discountDialogState = null
            },
            onDismiss = { discountDialogState = null },
        )
    }

    if (showDialogState) {
        AlertDialog(
            onDismissRequest = { showDialogState = false },
            title = { Text(stringResource(Res.string.dialog_transaction_confirm)) },
            text = {
                Column {
                    Text(stringResource(Res.string.message_confirm_transaction))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.label_total_amount, uiState.cart.finalTotal.format()),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(Res.string.label_product_count, uiState.cart.totalQuantity),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        registerScreenViewModel.confirmTransaction()
                        showDialogState = false
                    },
                ) {
                    Text(stringResource(Res.string.button_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialogState = false },
                ) {
                    Text(stringResource(Res.string.button_cancel))
                }
            },
        )
    }
}

private data class QuantityDialogState(
    val index: Int,
    val cartItem: CartItem,
)

private data class DiscountDialogState(
    val index: Int,
    val cartItem: CartItem,
)

@Composable
private fun InputModeSelector(
    selectedMode: InputMode,
    onModeSelected: (InputMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = listOf(InputMode.SEARCH, InputMode.CAMERA)
    val selectedIndex = options.indexOf(selectedMode)

    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth(),
    ) {
        options.forEachIndexed { index, mode ->
            SegmentedButton(
                selected = index == selectedIndex,
                onClick = { onModeSelected(mode) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size,
                ),
            ) {
                Text(
                    text = when (mode) {
                        InputMode.SEARCH -> stringResource(Res.string.input_mode_search)
                        InputMode.CAMERA -> stringResource(Res.string.input_mode_camera)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ProductSearchArea(
    modifier: Modifier = Modifier,
    searchQuery: String,
    sortOption: ProductSortOption,
    searchResults: Flow<PagingData<ProductDataModel>>?,
    onSearchQueryChange: (String) -> Unit,
    onSortOptionChange: (ProductSortOption) -> Unit,
    onProductSelected: (ProductDataModel) -> Unit,
) {
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(Res.string.content_desc_search),
                    )
                },
                placeholder = { Text(stringResource(Res.string.placeholder_search_product)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )

            Box {
                OutlinedButton(
                    onClick = { showSortMenu = true },
                ) {
                    Text(
                        text = when (sortOption) {
                            ProductSortOption.NAME_ASC -> stringResource(Res.string.sort_name_asc)
                            ProductSortOption.NAME_DESC -> stringResource(Res.string.sort_name_desc)
                            ProductSortOption.PRICE_ASC -> stringResource(Res.string.sort_price_asc)
                            ProductSortOption.PRICE_DESC -> stringResource(Res.string.sort_price_desc)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_name_asc)) },
                        onClick = {
                            onSortOptionChange(ProductSortOption.NAME_ASC)
                            showSortMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_name_desc)) },
                        onClick = {
                            onSortOptionChange(ProductSortOption.NAME_DESC)
                            showSortMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_price_asc)) },
                        onClick = {
                            onSortOptionChange(ProductSortOption.PRICE_ASC)
                            showSortMenu = false
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sort_price_desc)) },
                        onClick = {
                            onSortOptionChange(ProductSortOption.PRICE_DESC)
                            showSortMenu = false
                        },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        searchResults?.let { pagingFlow ->
            val lazyPagingItems = pagingFlow.collectAsLazyPagingItems()

            LazyHorizontalGrid(
                rows = GridCells.Adaptive(72.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(lazyPagingItems.itemCount) { index ->
                    lazyPagingItems[index]?.let { product ->
                        ProductSearchItem(
                            product = product,
                            onClick = { onProductSelected(product) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSearchItem(
    product: ProductDataModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .aspectRatio(2f)
            .clickable { onClick() },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Text(
                text = product.price.format(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RightPanel(
    modifier: Modifier = Modifier,
    uiState: RegisterUiState,
    onQuantityUpdate: (Int, Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onApplyDiscount: (Int) -> Unit,
    onShowQuantityDialog: (Int, CartItem) -> Unit = { _, _ -> },
    onShowDiscountDialog: (Int, CartItem) -> Unit = { _, _ -> },
    onShowDeleteDialog: (Int, CartItem) -> Unit = { _, _ -> },
    onShowConfirmDialog: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        CartArea(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cart = uiState.cart,
            onShowQuantityDialog = onShowQuantityDialog,
            onShowDiscountDialog = onShowDiscountDialog,
            onShowDeleteDialog = onShowDeleteDialog,
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        TotalArea(
            modifier = Modifier
                .fillMaxWidth(),
            cart = uiState.cart,
        )

        Button(
            onClick = onShowConfirmDialog,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = uiState.cart.items.isNotEmpty(),
        ) {
            Text(stringResource(Res.string.button_confirm))
        }
    }
}

@Composable
private fun CartArea(
    modifier: Modifier = Modifier,
    cart: Cart,
    onShowQuantityDialog: (Int, CartItem) -> Unit = { _, _ -> },
    onShowDiscountDialog: (Int, CartItem) -> Unit = { _, _ -> },
    onShowDeleteDialog: (Int, CartItem) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(Res.string.label_cart),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            itemsIndexed(cart.items) { index, cartItem ->
                CartItemRow(
                    cartItem = cartItem,
                    onShowQuantityDialog = { onShowQuantityDialog(index, cartItem) },
                    onShowDiscountDialog = { onShowDiscountDialog(index, cartItem) },
                    onShowDeleteDialog = { onShowDeleteDialog(index, cartItem) },
                )
                if (index < cart.items.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    cartItem: CartItem,
    onShowQuantityDialog: () -> Unit = {},
    onShowDiscountDialog: () -> Unit = {},
    onShowDeleteDialog: () -> Unit = {},
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = cartItem.product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.label_price_quantity, cartItem.unitPrice.format(), cartItem.quantity),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (cartItem.discountPercent > 0) {
                Text(
                    text = stringResource(Res.string.label_discount, cartItem.discountPercent),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = cartItem.totalPrice.format(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )

            Box {
                IconButton(
                    onClick = { showMenu = true },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(Res.string.content_desc_menu),
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_change_quantity)) },
                        onClick = {
                            showMenu = false
                            onShowQuantityDialog()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_apply_discount)) },
                        onClick = {
                            showMenu = false
                            onShowDiscountDialog()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.menu_delete)) },
                        onClick = {
                            showMenu = false
                            onShowDeleteDialog()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TotalArea(
    modifier: Modifier = Modifier,
    cart: Cart,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Res.string.label_product_count_prefix))
            Text(stringResource(Res.string.label_items_count, cart.totalQuantity))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(Res.string.label_subtotal))
            Text(cart.originalSubtotal.format())
        }

        if (cart.totalDiscountAmount.isPositive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.label_discount_prefix),
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = stringResource(Res.string.label_discount_amount, cart.totalDiscountAmount.format()),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.label_total),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = cart.finalTotal.format(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
